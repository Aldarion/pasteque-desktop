//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2007-2009 Openbravo, S.L.
//    http://www.openbravo.com/product/pos
//
//    This file is part of Openbravo POS.
//
//    Openbravo POS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Openbravo POS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Openbravo POS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.config;

import com.openbravo.pos.forms.AppConfig;
import com.openbravo.pos.util.AltEncrypter;

/**
 * Database section for configuration panel
 */
public class JPanelConfigDatabase extends PanelConfig {
    
    /** Creates new form JPanelConfigDatabase */
    public JPanelConfigDatabase() {
        super();        
        initComponents();
    }
   
    public void loadProperties(AppConfig config) {
        
        jtxtDbDriverLib.setText(config.getProperty("db.driverlib"));
        jtxtDbDriver.setText(config.getProperty("db.driver"));
        jtxtDbURL.setText(config.getProperty("db.URL"));
        
        String sDBUser = config.getProperty("db.user");
        String sDBPassword = config.getProperty("db.password");        
        if (sDBUser != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {
            // La clave esta encriptada.
            AltEncrypter cypher = new AltEncrypter("cypherkey" + sDBUser);
            sDBPassword = cypher.decrypt(sDBPassword.substring(6));
        }        
        jtxtDbUser.setText(sDBUser);
        jtxtDbPassword.setText(sDBPassword);   
        
        dirty.setDirty(false);
    }
   
    public void saveProperties(AppConfig config) {
        
        config.setProperty("db.driverlib", jtxtDbDriverLib.getText());
        config.setProperty("db.driver", jtxtDbDriver.getText());
        config.setProperty("db.URL", jtxtDbURL.getText());
        config.setProperty("db.user", jtxtDbUser.getText());
        AltEncrypter cypher = new AltEncrypter("cypherkey" + jtxtDbUser.getText());       
        config.setProperty("db.password", "crypt:" + cypher.encrypt(new String(jtxtDbPassword.getPassword())));

        dirty.setDirty(false);
    }
    
    private void initComponents() {
        this.addOptionsContainer("Label.Database");
        this.jtxtDbDriverLib = this.addFileParam("label.dbdriverlib");
        this.jtxtDbDriver = this.addTextParam("Label.DbDriver");
        this.jtxtDbURL = this.addTextParam("Label.DbURL");
        this.jtxtDbUser = this.addTextParam("Label.DbUser");
        this.jtxtDbPassword = this.addPasswordParam("Label.DbPassword");
    }
    
    private javax.swing.JTextField jtxtDbDriver;
    private javax.swing.JTextField jtxtDbDriverLib;
    private javax.swing.JPasswordField jtxtDbPassword;
    private javax.swing.JTextField jtxtDbURL;
    private javax.swing.JTextField jtxtDbUser;
    
}