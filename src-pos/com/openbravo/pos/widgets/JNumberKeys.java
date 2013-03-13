//    POS-Tech
//    Based upon Openbravo POS
//
//    Copyright (C) 2007-2009 Openbravo, S.L.
//                       2012 Scil (http://scil.coop)
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

package com.openbravo.pos.widgets;

import java.awt.ComponentOrientation;
import java.util.*;
import com.openbravo.data.loader.ImageLoader;
import com.openbravo.pos.forms.AppConfig;

public class JNumberKeys extends javax.swing.JPanel {

    private Vector m_Listeners = new Vector();
    
    private boolean minusenabled = true;
    private boolean equalsenabled = true;
    
    /** Creates new form JNumberKeys */
    public JNumberKeys() {
        initComponents ();
        
        m_jKey0.addActionListener(new MyKeyNumberListener('0'));
        m_jKey1.addActionListener(new MyKeyNumberListener('1'));
        m_jKey2.addActionListener(new MyKeyNumberListener('2'));
        m_jKey3.addActionListener(new MyKeyNumberListener('3'));
        m_jKey4.addActionListener(new MyKeyNumberListener('4'));
        m_jKey5.addActionListener(new MyKeyNumberListener('5'));
        m_jKey6.addActionListener(new MyKeyNumberListener('6'));
        m_jKey7.addActionListener(new MyKeyNumberListener('7'));
        m_jKey8.addActionListener(new MyKeyNumberListener('8'));
        m_jKey9.addActionListener(new MyKeyNumberListener('9'));
        m_jKeyDot.addActionListener(new MyKeyNumberListener('.'));
        m_jMultiply.addActionListener(new MyKeyNumberListener('*'));
        m_jCE.addActionListener(new MyKeyNumberListener('\u007f'));
        m_jPlus.addActionListener(new MyKeyNumberListener('+'));
        m_jMinus.addActionListener(new MyKeyNumberListener('-'));
        m_jEquals.addActionListener(new MyKeyNumberListener('='));
        m_jBack.addActionListener(new MyKeyNumberListener('\u0008'));
    }

    public void setNumbersOnly(boolean value) {
        m_jEquals.setVisible(value);
        m_jMinus.setVisible(value);
        m_jPlus.setVisible(value);
        m_jMultiply.setVisible(value);
    }
    
    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        
        m_jKey0.setEnabled(b);
        m_jKey1.setEnabled(b);
        m_jKey2.setEnabled(b);
        m_jKey3.setEnabled(b);
        m_jKey4.setEnabled(b);
        m_jKey5.setEnabled(b);
        m_jKey6.setEnabled(b);
        m_jKey7.setEnabled(b);
        m_jKey8.setEnabled(b);
        m_jKey9.setEnabled(b);
        m_jKeyDot.setEnabled(b);
        m_jMultiply.setEnabled(b);
        m_jCE.setEnabled(b);
        m_jPlus.setEnabled(b);
        m_jMinus.setEnabled(minusenabled && b);
        m_jEquals.setEnabled(equalsenabled && b);
        m_jBack.setEnabled(b);
    }
    
    @Override
    public void setComponentOrientation(ComponentOrientation o) {
        // Nothing to change
    }
    
    public void setMinusEnabled(boolean b) {
        minusenabled = b;
        m_jMinus.setEnabled(minusenabled && isEnabled());
    }
    
    public boolean isMinusEnabled() {
        return minusenabled;
    }
    
    public void setEqualsEnabled(boolean b) {
        equalsenabled = b;
        m_jEquals.setEnabled(equalsenabled && isEnabled());
    }
    
    public boolean isEqualsEnabled() {
        return equalsenabled;
    }

    
    public boolean isNumbersOnly() {
        return m_jEquals.isVisible();
    }
    
    public void addJNumberEventListener(JNumberEventListener listener) {
        m_Listeners.add(listener);
    }
    public void removeJNumberEventListener(JNumberEventListener listener) {
        m_Listeners.remove(listener);
    }
    
    private class MyKeyNumberListener implements java.awt.event.ActionListener {
        
        private char m_cCad;
        
        public MyKeyNumberListener(char cCad){
            m_cCad = cCad;
        }
        public void actionPerformed(java.awt.event.ActionEvent evt) {
           
            JNumberEvent oEv = new JNumberEvent(JNumberKeys.this, m_cCad);            
            JNumberEventListener oListener;
            
            for (Enumeration e = m_Listeners.elements(); e.hasMoreElements();) {
                oListener = (JNumberEventListener) e.nextElement();
                oListener.keyPerformed(oEv);
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        AppConfig cfg = AppConfig.loadedInstance;
        java.awt.GridBagConstraints gridBagConstraints;
        int btnspacing = WidgetsBuilder.pixelSize(Float.parseFloat(cfg.getProperty("ui.touchbtnspacing")));

        m_jBack = WidgetsBuilder.createButton(ImageLoader.readImageIcon("kpad_back.png"), WidgetsBuilder.SIZE_BIG);
        m_jCE = WidgetsBuilder.createButton(ImageLoader.readImageIcon("kpad_ce.png"), WidgetsBuilder.SIZE_BIG);
        m_jMultiply = WidgetsBuilder.createButton(ImageLoader.readImageIcon("kpad_mult.png"), WidgetsBuilder.SIZE_BIG);
        m_jMinus = WidgetsBuilder.createButton(ImageLoader.readImageIcon("kpad_minus.png"), WidgetsBuilder.SIZE_BIG);
        m_jPlus = WidgetsBuilder.createButton(ImageLoader.readImageIcon("kpad_plus.png"), WidgetsBuilder.SIZE_BIG);
        m_jKey9 = WidgetsBuilder.createButton(ImageLoader.readImageIcon("kpad_9.png"), WidgetsBuilder.SIZE_BIG);
        m_jKey8 = WidgetsBuilder.createButton(ImageLoader.readImageIcon("kpad_8.png"), WidgetsBuilder.SIZE_BIG);
        m_jKey7 = WidgetsBuilder.createButton(ImageLoader.readImageIcon("kpad_7.png"), WidgetsBuilder.SIZE_BIG);
        m_jKey4 = WidgetsBuilder.createButton(ImageLoader.readImageIcon("kpad_4.png"), WidgetsBuilder.SIZE_BIG);
        m_jKey5 = WidgetsBuilder.createButton(ImageLoader.readImageIcon("kpad_5.png"), WidgetsBuilder.SIZE_BIG);
        m_jKey6 = WidgetsBuilder.createButton(ImageLoader.readImageIcon("kpad_6.png"), WidgetsBuilder.SIZE_BIG);
        m_jKey3 = WidgetsBuilder.createButton(ImageLoader.readImageIcon("kpad_3.png"), WidgetsBuilder.SIZE_BIG);
        m_jKey2 = WidgetsBuilder.createButton(ImageLoader.readImageIcon("kpad_2.png"), WidgetsBuilder.SIZE_BIG);
        m_jKey1 = WidgetsBuilder.createButton(ImageLoader.readImageIcon("kpad_1.png"), WidgetsBuilder.SIZE_BIG);
        m_jKey0 = WidgetsBuilder.createButton(ImageLoader.readImageIcon("kpad_0.png"), WidgetsBuilder.SIZE_BIG);
        m_jKeyDot = WidgetsBuilder.createButton(ImageLoader.readImageIcon("kpad_dot.png"), WidgetsBuilder.SIZE_BIG);
        m_jEquals = WidgetsBuilder.createButton(ImageLoader.readImageIcon("kpad_enter.png"), WidgetsBuilder.SIZE_BIG);

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(5, 5, 5, 5)));
        m_jCE.setFocusPainted(false);
        m_jCE.setFocusable(false);
        m_jCE.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jCE.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(m_jCE, gridBagConstraints);

        m_jBack.setFocusPainted(false);
        m_jBack.setFocusable(false);
        m_jBack.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jBack.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, btnspacing, 0, 0);
        add(m_jBack, gridBagConstraints);

        m_jMultiply.setFocusPainted(false);
        m_jMultiply.setFocusable(false);
        m_jMultiply.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jMultiply.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, btnspacing, 0, 0);
        add(m_jMultiply, gridBagConstraints);

        m_jMinus.setFocusPainted(false);
        m_jMinus.setFocusable(false);
        m_jMinus.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jMinus.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, btnspacing, 0, 0);
        add(m_jMinus, gridBagConstraints);

        m_jPlus.setFocusPainted(false);
        m_jPlus.setFocusable(false);
        m_jPlus.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jPlus.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(btnspacing, btnspacing, 0, 0);
        add(m_jPlus, gridBagConstraints);

        m_jKey9.setFocusPainted(false);
        m_jKey9.setFocusable(false);
        m_jKey9.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jKey9.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(btnspacing, btnspacing, 0, 0);
        add(m_jKey9, gridBagConstraints);

        m_jKey8.setFocusPainted(false);
        m_jKey8.setFocusable(false);
        m_jKey8.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jKey8.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(btnspacing, btnspacing, 0, 0);
        add(m_jKey8, gridBagConstraints);

        m_jKey7.setFocusPainted(false);
        m_jKey7.setFocusable(false);
        m_jKey7.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jKey7.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(btnspacing, 0, 0, 0);
        add(m_jKey7, gridBagConstraints);

        m_jKey4.setFocusPainted(false);
        m_jKey4.setFocusable(false);
        m_jKey4.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jKey4.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(btnspacing, 0, 0, 0);
        add(m_jKey4, gridBagConstraints);

        m_jKey5.setFocusPainted(false);
        m_jKey5.setFocusable(false);
        m_jKey5.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jKey5.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(btnspacing, btnspacing, 0, 0);
        add(m_jKey5, gridBagConstraints);

        m_jKey6.setFocusPainted(false);
        m_jKey6.setFocusable(false);
        m_jKey6.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jKey6.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(btnspacing, btnspacing, 0, 0);
        add(m_jKey6, gridBagConstraints);

        m_jKey3.setFocusPainted(false);
        m_jKey3.setFocusable(false);
        m_jKey3.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jKey3.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(btnspacing, btnspacing, 0, 0);
        add(m_jKey3, gridBagConstraints);

        m_jKey2.setFocusPainted(false);
        m_jKey2.setFocusable(false);
        m_jKey2.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jKey2.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(btnspacing, btnspacing, 0, 0);
        add(m_jKey2, gridBagConstraints);

        m_jKey1.setFocusPainted(false);
        m_jKey1.setFocusable(false);
        m_jKey1.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jKey1.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(btnspacing, 0, 0, 0);
        add(m_jKey1, gridBagConstraints);

        m_jKey0.setFocusPainted(false);
        m_jKey0.setFocusable(false);
        m_jKey0.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jKey0.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(btnspacing, 0, 0, 0);
        add(m_jKey0, gridBagConstraints);

        m_jKeyDot.setFocusPainted(false);
        m_jKeyDot.setFocusable(false);
        m_jKeyDot.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jKeyDot.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(btnspacing, btnspacing, 0, 0);
        add(m_jKeyDot, gridBagConstraints);

        m_jEquals.setFocusPainted(false);
        m_jEquals.setFocusable(false);
        m_jEquals.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jEquals.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(btnspacing, btnspacing, 0, 0);
        add(m_jEquals, gridBagConstraints);
        
        // Force maximum size to preferred to avoid keyboard from stretching
        // in dynamic layouts
        this.setMaximumSize(this.getPreferredSize());
    }
    // </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton m_jBack;
    private javax.swing.JButton m_jCE;
    private javax.swing.JButton m_jEquals;
    private javax.swing.JButton m_jKey0;
    private javax.swing.JButton m_jKey1;
    private javax.swing.JButton m_jKey2;
    private javax.swing.JButton m_jKey3;
    private javax.swing.JButton m_jKey4;
    private javax.swing.JButton m_jKey5;
    private javax.swing.JButton m_jKey6;
    private javax.swing.JButton m_jKey7;
    private javax.swing.JButton m_jKey8;
    private javax.swing.JButton m_jKey9;
    private javax.swing.JButton m_jKeyDot;
    private javax.swing.JButton m_jMinus;
    private javax.swing.JButton m_jMultiply;
    private javax.swing.JButton m_jPlus;
    // End of variables declaration//GEN-END:variables

}
