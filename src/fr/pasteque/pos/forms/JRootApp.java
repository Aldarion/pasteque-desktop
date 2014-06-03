//    POS-Tech
//    Based upon Openbravo POS
//
//    Copyright (C) 2007-2009 Openbravo, S.L.
//                       2012 SARL SCOP Scil (http://scil.coop)
//
//    This file is part of POS-Tech.
//
//    POS-Tech is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    POS-Tech is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with POS-Tech.  If not, see <http://www.gnu.org/licenses/>.

package fr.pasteque.pos.forms;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.sql.SQLException;
import javax.swing.*;

import fr.pasteque.pos.printer.*;

import fr.pasteque.beans.*;

import fr.pasteque.basic.BasicException;
import fr.pasteque.data.gui.MessageInf;
import fr.pasteque.data.gui.JMessageDialog;
import fr.pasteque.data.loader.ImageLoader;
import fr.pasteque.pos.caching.CallQueue;
import fr.pasteque.pos.caching.LocalDB;
import fr.pasteque.pos.caching.ResourcesCache;
import fr.pasteque.pos.customers.DataLogicCustomers;
import fr.pasteque.pos.forms.AppConfig;
import fr.pasteque.pos.scale.DeviceScale;
import fr.pasteque.pos.scanpal2.DeviceScanner;
import fr.pasteque.pos.scanpal2.DeviceScannerFactory;
import fr.pasteque.pos.ticket.CashRegisterInfo;
import fr.pasteque.pos.ticket.CashSession;
import fr.pasteque.pos.widgets.WidgetsBuilder;
import java.sql.SQLException;
import java.util.Locale;
import java.util.regex.Matcher;

/**
 *
 * @author adrianromero
 */
public class JRootApp extends JPanel implements AppView {

    private AppProperties m_props;
    private DataLogicSystem m_dlSystem;
    
    private Properties m_propsdb = null;
    private CashSession activeCashSession;
    private CashRegisterInfo cashRegister;
    
    private String m_sInventoryLocation;
    
    private StringBuffer inputtext;
   
    private DeviceScale m_Scale;
    private DeviceScanner m_Scanner;
    private DeviceTicket m_TP;   
    private TicketParser m_TTP;
    
    private Map<String, BeanFactory> m_aBeanFactories;
    
    private JPrincipalApp m_principalapp = null;
    
    private static HashMap<String, String> m_oldclasses; // This is for backwards compatibility purposes
    
    static {        
        initOldClasses();
    }
    
    /** Creates new form JRootApp */
    public JRootApp() {    

        m_aBeanFactories = new HashMap<String, BeanFactory>();
        
        initComponents ();            
        jScrollPane1.getVerticalScrollBar().setPreferredSize(new Dimension(35, 35));   
    }
    
    public boolean initApp(AppProperties props) {
        
        m_props = props;

        // support for different component orientation languages.
        applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        
        m_dlSystem = new DataLogicSystem();
        DataLogicCustomers dlCust = new DataLogicCustomers();
        
        // Check database compatibility
        String sDBVersion = readDataBaseVersion();
        while (!AppLocal.DB_VERSION.equals(sDBVersion)) {
            // TODO: i18n
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_DANGER, "Incompatbile version", "Server version " + sDBVersion + ", expected " + AppLocal.DB_VERSION));
            return false;
        }     
        
        // Load host properties
        try {
            this.cashRegister = m_dlSystem.getCashRegister(m_props.getHost());
        } catch (BasicException e) {
            JMessageDialog.showMessage(this,
                    new MessageInf(MessageInf.SGN_DANGER, e.getMessage(), e));
            return false;
        }
        if (this.cashRegister == null) {
            // Unknown cash register
            JMessageDialog.showMessage(this,
                    new MessageInf(MessageInf.SGN_DANGER,
                            AppLocal.getIntString("Message.UnknownCash")));
            return false;
        }

        // Load cash session
        try {
            CashSession cashSess = m_dlSystem.getCashSession(this.cashRegister.getId());
            if (cashSess == null) {
                // New cash session
                this.newActiveCash();
            } else {
                this.setActiveCash(cashSess);
                if (cashSess.getId() != null) {
                    CallQueue.setup(cashSess.getId());
                }
            }
        } catch (BasicException e) {
            // Casco. Sin caja no hay pos
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotclosecash"), e);
            msg.show(this);
            return false;
        }  
        
        // Load location
        m_sInventoryLocation = this.cashRegister.getLocationId();
        if (m_sInventoryLocation == null) {
            // Not set, use default
            m_sInventoryLocation = "0";
        }
        
        // Inicializo la impresora...
        m_TP = new DeviceTicket(this, m_props);
        
        // Inicializamos 
        m_TTP = new TicketParser(getDeviceTicket(), m_dlSystem);
        printerStart();
        
        // Inicializamos la bascula
        m_Scale = new DeviceScale(this, m_props);
        
        // Inicializamos la scanpal
        m_Scanner = DeviceScannerFactory.createInstance(m_props);
            
        // Leemos los recursos basicos
        BufferedImage imgicon = m_dlSystem.getResourceAsImage("Window.Logo");
        m_jLblTitle.setIcon(imgicon == null ? null : new ImageIcon(imgicon));
        m_jLblTitle.setText(m_dlSystem.getResourceAsText("Window.Title"));  
        
        String sWareHouse;
        try {
            sWareHouse = m_dlSystem.findLocationName(m_sInventoryLocation);
        } catch (BasicException e) {
            sWareHouse = null; // no he encontrado el almacen principal
        }

        DataLogicSales m_dlSales = new DataLogicSales();
        // Preload caches
        m_dlSystem.preloadUsers();
        m_dlSystem.preloadRoles();
        // Reload resources cache
        java.util.List<String> cachedRes = ResourcesCache.list();
        for (String res : cachedRes) {
            m_dlSystem.preloadResource(res);
        }
        // Init local cache
        try {
            LocalDB.init();
            m_dlSales.preloadCategories();
            m_dlSales.preloadProducts();
            m_dlSales.preloadTaxes();
            m_dlSales.preloadCurrencies();
            m_dlSales.preloadTariffAreas();
            m_dlSales.preloadCompositions();
            dlCust.preloadCustomers();
            m_dlSystem.preloadCashRegisters();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Initialize currency format
        try {
            fr.pasteque.format.Formats.setDefaultCurrency(m_dlSales.getMainCurrency());
        } catch (BasicException e) {
            e.printStackTrace();
        }

        this.showLogin();

        return true;
    }
    
    private String readDataBaseVersion() {
        try {
            return m_dlSystem.findDbVersion();
        } catch (BasicException ed) {
            return null;
        }
    }
    
    public void tryToClose() {   
        
        if (closeAppView()) {

            // success. continue with the shut down

            // apago el visor
            m_TP.getDeviceDisplay().clearVisor();

            // Download Root form
            SwingUtilities.getWindowAncestor(this).dispose();
        }
    }
    
    // Interfaz de aplicacion
    public DeviceTicket getDeviceTicket(){
        return m_TP;
    }
    
    public DeviceScale getDeviceScale() {
        return m_Scale;
    }
    public DeviceScanner getDeviceScanner() {
        return m_Scanner;
    }
    
    public CashRegisterInfo getCashRegister() {
        return this.cashRegister;
    }
    public String getInventoryLocation() {
        return m_sInventoryLocation;
    }
    public CashSession getActiveCashSession() {
        return this.activeCashSession;
    }
    public String getActiveCashIndex() {
        return this.activeCashSession.getId();
    }
    public int getActiveCashSequence() {
        return this.activeCashSession.getSequence();
    }
    public Date getActiveCashDateStart() {
        return this.activeCashSession.getOpenDate();
    }
    
    public boolean isCashOpened() {
        return this.activeCashSession.isOpened();
    }
    
    public void setActiveCash(CashSession cashSess) {
        this.activeCashSession = cashSess;
    }

    public void newActiveCash() {
        this.activeCashSession = new CashSession(null,
                this.cashRegister.getId(), 0, null, null, null, null, null);
    }

    public AppProperties getProperties() {
        return m_props;
    }
    
    public Object getBean(String beanfactory) throws BeanFactoryException {
        
        // For backwards compatibility
        beanfactory = mapNewClass(beanfactory);
        
        
        BeanFactory bf = m_aBeanFactories.get(beanfactory);
        if (bf == null) {   
            
            // testing sripts
            if (beanfactory.startsWith("/")) {
                bf = new BeanFactoryScript(beanfactory);               
            } else {
                // Class BeanFactory
                try {
                    Class bfclass = Class.forName(beanfactory);

                    if (BeanFactory.class.isAssignableFrom(bfclass)) {
                        bf = (BeanFactory) bfclass.newInstance();             
                    } else {
                        // the old construction for beans...
                        Constructor constMyView = bfclass.getConstructor(new Class[] {AppView.class});
                        Object bean = constMyView.newInstance(new Object[] {this});

                        bf = new BeanFactoryObj(bean);
                    }

                } catch (Exception e) {
                    // ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
                    throw new BeanFactoryException(e);
                }
            }
            
            // cache the factory
            m_aBeanFactories.put(beanfactory, bf);         
            
            // Initialize if it is a BeanFactoryApp
            if (bf instanceof BeanFactoryApp) {
                ((BeanFactoryApp) bf).init(this);
            }
        }
        return bf.getBean();
    }
    
    private static String mapNewClass(String classname) {
        String newclass = m_oldclasses.get(classname);
        return newclass == null 
                ? classname 
                : newclass;
    }
    
    private static void initOldClasses() {
        m_oldclasses = new HashMap<String, String>();
        
        // update bean names from 2.00 to 2.20    
        m_oldclasses.put("fr.pasteque.pos.reports.JReportCustomers", "/com/openbravo/reports/customers.bs");
        m_oldclasses.put("fr.pasteque.pos.reports.JReportCustomersB", "/com/openbravo/reports/customersb.bs");
        m_oldclasses.put("fr.pasteque.pos.reports.JReportClosedPos", "/com/openbravo/reports/closedpos.bs");
        m_oldclasses.put("fr.pasteque.pos.reports.JReportClosedProducts", "/com/openbravo/reports/closedproducts.bs");
        m_oldclasses.put("fr.pasteque.pos.reports.JChartSales", "/com/openbravo/reports/chartsales.bs");
        m_oldclasses.put("fr.pasteque.pos.reports.JReportInventory", "/com/openbravo/reports/inventory.bs");
        m_oldclasses.put("fr.pasteque.pos.reports.JReportInventory2", "/com/openbravo/reports/inventoryb.bs");
        m_oldclasses.put("fr.pasteque.pos.reports.JReportInventoryBroken", "/com/openbravo/reports/inventorybroken.bs");
        m_oldclasses.put("fr.pasteque.pos.reports.JReportInventoryDiff", "/com/openbravo/reports/inventorydiff.bs");
        m_oldclasses.put("fr.pasteque.pos.reports.JReportPeople", "/com/openbravo/reports/people.bs");
        m_oldclasses.put("fr.pasteque.pos.reports.JReportTaxes", "/com/openbravo/reports/taxes.bs");
        m_oldclasses.put("fr.pasteque.pos.reports.JReportUserSales", "/com/openbravo/reports/usersales.bs");
        m_oldclasses.put("fr.pasteque.pos.reports.JReportProducts", "/com/openbravo/reports/products.bs");
        m_oldclasses.put("fr.pasteque.pos.reports.JReportCatalog", "/com/openbravo/reports/productscatalog.bs");
        
        // update bean names from 2.10 to 2.20
        m_oldclasses.put("fr.pasteque.pos.panels.JPanelTax", "com.openbravo.pos.inventory.TaxPanel");
       
    }
    
    public void waitCursorBegin() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
    
    public void waitCursorEnd(){
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    public AppUserView getAppUserView() {
        return m_principalapp;
    }

    
    private void printerStart() {
        
        String sresource = m_dlSystem.getResourceAsXML("Printer.Start");
        if (sresource == null) {
            m_TP.getDeviceDisplay().writeVisor(AppLocal.APP_NAME, AppLocal.APP_VERSION);
        } else {
            try {
                m_TTP.printTicket(sresource);
            } catch (TicketPrinterException eTP) {
                m_TP.getDeviceDisplay().writeVisor(AppLocal.APP_NAME, AppLocal.APP_VERSION);
            }
        }        
    }
    
    private void listPeople() {
        
        try {
           
            jScrollPane1.getViewport().setView(null);

            JFlowPanel jPeople = new JFlowPanel();
            jPeople.applyComponentOrientation(getComponentOrientation());
            
            java.util.List people = m_dlSystem.listPeopleVisible();
            String[] enabledUsers = m_props.getEnabledUsers();
            
            for (int i = 0; i < people.size(); i++) {
                 
                AppUser user = (AppUser) people.get(i);
                // Check if user is not disabled on this machine
                if (enabledUsers != null) {
                    boolean enabled = false;
                    for (String id : enabledUsers) {
                        if (id.equals(user.getId())) {
                            enabled = true;
                            break;
                        }
                    }
                    if (!enabled) {
                        continue;
                    }
                }

                JButton btn = new JButton(new AppUserAction(user));
                btn.applyComponentOrientation(getComponentOrientation());
                btn.setFocusPainted(false);
                btn.setFocusable(false);
                btn.setRequestFocusEnabled(false);
                btn.setHorizontalAlignment(SwingConstants.LEADING);
                btn.setMaximumSize(new Dimension(150, 50));
                btn.setPreferredSize(new Dimension(150, 50));
                btn.setMinimumSize(new Dimension(150, 50));
        
                jPeople.add(btn);                    
            }
            jScrollPane1.getViewport().setView(jPeople);
            
        } catch (BasicException ee) {
            ee.printStackTrace();
        }
    }
    // Action performed when clicking on a people button
    private class AppUserAction extends AbstractAction {
        
        private AppUser m_actionuser;
        
        public AppUserAction(AppUser user) {
            m_actionuser = user;
            putValue(Action.SMALL_ICON, m_actionuser.getIcon());
            putValue(Action.NAME, m_actionuser.getName());
        }
        
        public AppUser getUser() {
            return m_actionuser;
        }
        
        public void actionPerformed(ActionEvent evt) {
            // Try auto-logging if user has no password set
            if (m_actionuser.authenticate()) {
                // It works!
                openAppView(m_actionuser);         
            } else {
                // Show password input
                String sPassword = JPasswordDialog.showEditPassword(JRootApp.this, 
                        AppLocal.getIntString("Label.Password"),
                        m_actionuser.getName(),
                        m_actionuser.getIcon());
                if (sPassword != null) {
                    if (m_actionuser.authenticate(sPassword)) {
                        // Password is valid, enter app
                        openAppView(m_actionuser);                
                    } else {
                        // Wrong password, show message
                        MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.BadPassword"));
                        msg.show(JRootApp.this);                        
                    }
                }   
            }
        }
    }
    
    private void showView(String view) {
        CardLayout cl = (CardLayout)(m_jPanelContainer.getLayout());
        cl.show(m_jPanelContainer, view);  
    }
    
    /** Enter app, show main screen with selected user. */
    private void openAppView(AppUser user) {
        // Make sure app is not already opened before continuing
        if (closeAppView()) {

            m_principalapp = new JPrincipalApp(this, user);

            // The user status notificator
            jPanel3.add(m_principalapp.getNotificator());
            jPanel3.revalidate();
            
            // The main panel
            m_jPanelContainer.add(m_principalapp, "_" + m_principalapp.getUser().getId());
            showView("_" + m_principalapp.getUser().getId());

            m_principalapp.activate();
        }
    }
    
    /** Return to login screen.
     * @return True if not opened or successfuly closed, false if close failed.
     */
    public boolean closeAppView() {
        
        if (m_principalapp == null) {
            return true;
        } else if (!m_principalapp.deactivate()) {
            return false;
        } else {
            // the status label
            jPanel3.remove(m_principalapp.getNotificator());
            jPanel3.revalidate();
            jPanel3.repaint();

            // remove the card
            m_jPanelContainer.remove(m_principalapp);
            m_principalapp = null;

            showLogin();
            
            return true;
        }
    }
    
    private void showLogin() {
        
        // Show Login
        listPeople();
        showView("login");     

        // show welcome message
        printerStart();
 
        // keyboard listener activation
        inputtext = new StringBuffer();
        m_txtKeys.setText(null);       
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                m_txtKeys.requestFocus();
            }
        });  
    }
    
    private void processKey(char c) {
        
        if (c == '\n') {
            
            AppUser user = null;
            try {
                user = m_dlSystem.findPeopleByCard(inputtext.toString());
            } catch (BasicException e) {
                e.printStackTrace();
            }
            
            if (user == null)  {
                // user not found
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.nocard"));
                msg.show(this);                
            } else {
                openAppView(user);   
            }

            inputtext = new StringBuffer();
        } else {
            inputtext.append(c);
        }
    }

    private void initComponents() {
        AppConfig cfg = AppConfig.loadedInstance;

        m_jPanelTitle = new javax.swing.JPanel();
        m_jLblTitle = new javax.swing.JLabel();
        poweredby = new javax.swing.JLabel();
        m_jPanelContainer = new javax.swing.JPanel();
        m_jPanelLogin = new javax.swing.JPanel();
        postechLogo = new javax.swing.JLabel();
        m_jLogonName = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        m_jClose = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        m_txtKeys = new javax.swing.JTextField();
        m_jPanelDown = new javax.swing.JPanel();
        panelTask = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(1024, 768));
        setLayout(new java.awt.BorderLayout());

        m_jPanelContainer.setLayout(new java.awt.CardLayout());

        // Main login content
        m_jPanelLogin.setLayout(new GridBagLayout());
        GridBagConstraints c = null;

        // Logo
        postechLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        postechLogo.setIcon(ImageLoader.readImageIcon("logo.png",
                cfg.getLocale()));
        postechLogo.setAlignmentX(0.5F);
        postechLogo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        postechLogo.setMaximumSize(new java.awt.Dimension(800, 1024));
        postechLogo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 3;
        m_jPanelLogin.add(postechLogo, c);
        // Login frame
        m_jLogonName.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        m_jLogonName.setLayout(new java.awt.BorderLayout());
        // Scroll
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(510, 118));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(510, 118));
        m_jLogonName.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        jPanel2.setLayout(new java.awt.BorderLayout());
        // Close button
        m_jClose.setIcon(ImageLoader.readImageIcon("exit.png"));
        m_jClose.setText(AppLocal.getIntString("Button.Close")); // NOI18N
        m_jClose.setFocusPainted(false);
        m_jClose.setFocusable(false);
        m_jClose.setPreferredSize(new java.awt.Dimension(115, 35));
        m_jClose.setRequestFocusEnabled(false);
        m_jClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jCloseActionPerformed(evt);
            }
        });
        jPanel2.add(m_jClose, java.awt.BorderLayout.NORTH);
        // Login by scan
        jPanel1.setLayout(null);
        m_txtKeys.setPreferredSize(new java.awt.Dimension(0, 0));
        m_txtKeys.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                m_txtKeysKeyTyped(evt);
            }
        });
        jPanel1.add(m_txtKeys);
        m_txtKeys.setBounds(0, 0, 0, 0);
        jPanel2.add(jPanel1, java.awt.BorderLayout.CENTER);
        m_jLogonName.add(jPanel2, java.awt.BorderLayout.LINE_END);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.SOUTH;
        m_jPanelLogin.add(m_jLogonName, c);
        // About
        JButton about = WidgetsBuilder.createButton(ImageLoader.readImageIcon("about.png"), WidgetsBuilder.SIZE_SMALL);
        about.setBorder(BorderFactory.createEmptyBorder());
        about.setContentAreaFilled(false);
        about.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutActionPerformed(evt);
            }
        });
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.weighty = 0.1;
        c.weightx = 1;
        int versionInset = WidgetsBuilder.dipToPx(5);
        c.insets = new Insets(0, 0, 0, versionInset);
        c.anchor = GridBagConstraints.LINE_END;
        m_jPanelLogin.add(about, c);
        m_jPanelContainer.add(m_jPanelLogin, "login");
        add(m_jPanelContainer, java.awt.BorderLayout.CENTER);

    }

    private void m_jCloseActionPerformed(java.awt.event.ActionEvent evt) {
        tryToClose();
    }

    private void aboutActionPerformed(java.awt.event.ActionEvent evt) {
        final JFrame about = new JFrame();
        about.setPreferredSize(new java.awt.Dimension(400, 300));
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        about.setBounds((screenSize.width-400)/2, (screenSize.height-300)/2, 400, 300);
        about.setLayout(new GridBagLayout());
        about.setIconImage(ImageLoader.readImage("favicon.png"));
        GridBagConstraints c = null;

        ImageIcon icon = ImageLoader.readImageIcon("favicon.png");
        JLabel iconLabel = new JLabel(icon);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.3;
        c.weighty = 0.3;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);
        about.add(iconLabel, c);

        String version = String.format(AppLocal.getIntString("Label.Version"),
                AppLocal.getIntString("Version.Code"), AppLocal.APP_VERSION);
        JLabel aboutLabel = WidgetsBuilder.createSmallLabel(version);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.7;
        c.weighty = 0.3;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);
        about.add(aboutLabel, c);

        String contact = AppLocal.getIntString("Label.AboutContact");
        JLabel contactLabel = WidgetsBuilder.createSmallLabel(contact);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.weightx = 1;
        c.weighty = 0.5;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);
        about.add(contactLabel, c);

        String licence = "<html>" + AppLocal.getIntString("Licence") + "</html>";
        JLabel licenceLabel = WidgetsBuilder.createSmallLabel(licence);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.weightx = 1;
        c.weighty = 0.7;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);
        about.add(licenceLabel, c);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        JButton site = WidgetsBuilder.createButton(ImageLoader.readImageIcon("button_web.png"),
                AppLocal.getIntString("Button.AboutSite"),
                WidgetsBuilder.SIZE_MEDIUM);
        site.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new java.net.URI(AppLocal.getIntString("Button.AboutSiteURL")));
                    } catch (Exception e) {
                        /* TODO: error handling */
                    }
                } else {
                    /* TODO: error handling */
                }
            }
        });
        buttonsPanel.add(site);
        JButton close = WidgetsBuilder.createButton(ImageLoader.readImageIcon("button_cancel.png"),
                AppLocal.getIntString("Button.Cancel"),
                WidgetsBuilder.SIZE_MEDIUM);
        close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                about.dispose();
            }
        });
        buttonsPanel.add(close);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.weightx = 1;
        c.anchor = GridBagConstraints.LINE_END;
        about.add(buttonsPanel, c);

        about.setTitle(AppLocal.APP_NAME + " - " + AppLocal.APP_VERSION);
        about.setLocationRelativeTo(null);
        about.pack();
        about.setVisible(true);
    }

    private void m_txtKeysKeyTyped(java.awt.event.KeyEvent evt) {
        m_txtKeys.setText("0");
        processKey(evt.getKeyChar());
    }

    private javax.swing.JLabel postechLogo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton m_jClose;
    private javax.swing.JLabel m_jLblTitle;
    private javax.swing.JPanel m_jLogonName;
    private javax.swing.JPanel m_jPanelContainer;
    private javax.swing.JPanel m_jPanelDown;
    private javax.swing.JPanel m_jPanelLogin;
    private javax.swing.JPanel m_jPanelTitle;
    private javax.swing.JTextField m_txtKeys;
    private javax.swing.JPanel panelTask;
    private javax.swing.JLabel poweredby;

}
