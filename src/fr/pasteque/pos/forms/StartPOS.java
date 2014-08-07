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

package fr.pasteque.pos.forms;

import fr.pasteque.data.loader.ImageLoader;
import fr.pasteque.format.Formats;
import fr.pasteque.pos.instance.InstanceQuery;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Locale;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UIManager;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.SubstanceSkin;

/**
 *
 * @author adrianromero
 */
public class StartPOS {

    private static Logger logger = Logger.getLogger("fr.pasteque.pos.forms.StartPOS");

    /** Creates a new instance of StartPOS */
    private StartPOS() {
    }

    public static boolean registerApp() {
        // vemos si existe alguna instancia
        InstanceQuery i = null;
        try {
            i = new InstanceQuery();
            i.getAppMessage().restoreWindow();
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public static void main (final String args[]) {
        // Load config
        final AppConfig config = new AppConfig(args);
        config.load();
        // set Locale.
        String slang = config.getProperty("user.language");
        String scountry = config.getProperty("user.country");
        String svariant = config.getProperty("user.variant");
        if (slang != null && !slang.equals("") && scountry != null
                && svariant != null) {
            Locale.setDefault(new Locale(slang, scountry, svariant));
        }
        String locale = config.getLocale();
        // Set logo according to language
        ImageIcon splashImage = ImageLoader.readImageIcon("logo.png", locale);
        // Show splash
        final JFrame splash = new JFrame();
        int splashWidth = 420;
        int splashHeight = 344;
        splash.setUndecorated(true);
        splash.setResizable(false);
        splash.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        splash.setPreferredSize(new java.awt.Dimension(splashWidth,
                        splashHeight));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int splashX = (screenSize.width - splashWidth) / 2;
        int splashY = (screenSize.height - splashHeight) / 2;
        splash.setBounds(splashX, splashY, splashWidth, splashHeight);
        JLabel splashLabel = new JLabel(splashImage);
        splash.add(splashLabel, BorderLayout.CENTER);
        splash.setTitle(AppLocal.APP_NAME + " - " + AppLocal.APP_VERSION);
        splash.setLocationRelativeTo(null);
        splash.pack();
        splash.setVisible(true);
        splash.setIconImage(ImageLoader.readImage("favicon.png"));

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (!registerApp()) {
                    System.exit(1);
                }
                
                // Set the format patterns
                Formats.setIntegerPattern(config.getProperty("format.integer"));
                Formats.setDoublePattern(config.getProperty("format.double"));
                Formats.setCurrencyPattern(config.getProperty("format.currency"));
                Formats.setPercentPattern(config.getProperty("format.percent"));
                Formats.setDatePattern(config.getProperty("format.date"));
                Formats.setTimePattern(config.getProperty("format.time"));
                Formats.setDateTimePattern(config.getProperty("format.datetime"));
                // Set the look and feel.
                try {
                    Object laf = Class.forName(config.getProperty("swing.defaultlaf")).newInstance();
                    if (laf instanceof LookAndFeel){
                        UIManager.setLookAndFeel((LookAndFeel) laf);
                    } else if (laf instanceof SubstanceSkin) {
                        SubstanceLookAndFeel.setSkin((SubstanceSkin) laf);
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Cannot set look and feel", e);
                }

                String screenmode = config.getProperty("machine.screenmode");
                if ("fullscreen".equals(screenmode)) {
                    JRootKiosk rootkiosk = new JRootKiosk();
                    rootkiosk.initFrame(config);
                } else {
                    JRootFrame rootframe = new JRootFrame();
                    rootframe.initFrame(config);
                }
                splash.dispose();
            }
        });
    }
}
