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

package com.openbravo.pos.sales.restaurant;

import javax.swing.*;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.widgets.JNumberDialog;
import com.openbravo.pos.widgets.WidgetsBuilder;

public class JTicketsBagRestaurant extends javax.swing.JPanel {
    
    private AppView m_App;
    private JTicketsBagRestaurantMap m_restaurant;
         
    /** Creates new form JTicketsBagRestaurantMap */
    public JTicketsBagRestaurant(AppView app, JTicketsBagRestaurantMap restaurant) {
        
        m_App = app;
        m_restaurant = restaurant;
        
        initComponents();
    }

    public void activate() {
        
        // Authorization
        m_jDelTicket.setEnabled(m_App.getAppUserView().getUser().hasPermission("com.openbravo.pos.sales.JPanelTicketEdits"));
    }
    
    private void initComponents() {

        m_jDelTicket = WidgetsBuilder.createButton(
                new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/editdelete.png")),
                AppLocal.getIntString("Button.DeleteTicket.Tooltip"));
        moveTicketBtn = WidgetsBuilder.createButton(
                new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/redo16.png")),
                AppLocal.getIntString("Button.MoveTable.Tooltip"));
        backBtn = WidgetsBuilder.createButton(
                new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/atlantikdesignersmall.png")),
                AppLocal.getIntString("Button.RestaurantMap.Tooltip"));
        JButton custCountBtn = WidgetsBuilder.createButton(
                new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/edit_group.png")),
                AppLocal.getIntString("Button.CustCount.Tooltip"));

        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        m_jDelTicket.setFocusPainted(false);
        m_jDelTicket.setFocusable(false);
        m_jDelTicket.setRequestFocusEnabled(false);
        m_jDelTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jDelTicketActionPerformed(evt);
            }
        });
        add(m_jDelTicket);

        moveTicketBtn.setFocusPainted(false);
        moveTicketBtn.setFocusable(false);
        moveTicketBtn.setRequestFocusEnabled(false);
        moveTicketBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveTicketBtnActionPerformed(evt);
            }
        });
        add(moveTicketBtn);

        backBtn.setFocusPainted(false);
        backBtn.setFocusable(false);
        backBtn.setRequestFocusEnabled(false);
        backBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnActionPerformed(evt);
            }
        });
        add(backBtn);

        custCountBtn.setFocusPainted(false);
        custCountBtn.setFocusable(false);
        custCountBtn.setRequestFocusEnabled(false);
        custCountBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                custCountBtnActionPerformed(evt);
            }
        });
        add(custCountBtn);

    }

    private void moveTicketBtnActionPerformed(java.awt.event.ActionEvent evt) {
        m_restaurant.moveTicket();
    }

    private void m_jDelTicketActionPerformed(java.awt.event.ActionEvent evt) {
        int res = JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.wannadelete"), AppLocal.getIntString("title.editor"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res == JOptionPane.YES_OPTION) {
            m_restaurant.deleteTicket();
        }
    }

    private void backBtnActionPerformed(java.awt.event.ActionEvent evt) {
        m_restaurant.newTicket();
        m_restaurant.loadTickets();
    }

    private void custCountBtnActionPerformed(java.awt.event.ActionEvent evt) {
        Double dblcount = JNumberDialog.showEditNumber(this, JNumberDialog.INT_POSITIVE, AppLocal.getIntString("Label.CustCount"), AppLocal.getIntString("Label.CustCountInput"), new ImageIcon(getClass().getResource("/com/openbravo/images/edit_group.png")));
        if (dblcount != null) {
            int count = (int) dblcount.doubleValue();
            m_restaurant.setCustomersCount(count);
        }
    }
    
    private javax.swing.JButton backBtn;
    private javax.swing.JButton moveTicketBtn;
    private javax.swing.JButton m_jDelTicket;
    
}

