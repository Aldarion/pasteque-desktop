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

package com.openbravo.pos.sales.simple;

import com.openbravo.pos.forms.*; 
import javax.swing.*;
import com.openbravo.pos.sales.*;
import com.openbravo.pos.ticket.TicketInfo;
import com.openbravo.pos.widgets.WidgetsBuilder;

public class JTicketsBagSimple extends JTicketsBag {
    
    /** Creates new form JTicketsBagSimple */
    public JTicketsBagSimple(AppView app, TicketsEditor panelticket) {
        
        super(app, panelticket);
        
        initComponents();
    }
    
    public void activate() {
        
        m_panelticket.setActiveTicket(new TicketInfo(), null);
        
        // Authorization
        m_jDelTicket.setEnabled(m_App.getAppUserView().getUser().hasPermission("com.openbravo.pos.sales.JPanelTicketEdits"));

    }
    public boolean deactivate() {
        m_panelticket.setActiveTicket(null, null);      
        return true;
    }
    
    public void deleteTicket() {           
        m_panelticket.setActiveTicket(new TicketInfo(), null);
    }
    
    protected JComponent getBagComponent() {
        return this;
    }
    protected JComponent getNullComponent() {
        return new JPanel();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        m_jDelTicket = WidgetsBuilder.createButton(
                new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/editdelete.png")),
                AppLocal.getIntString("Button.DeleteTicket.Tooltip"));

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

    }// </editor-fold>//GEN-END:initComponents

    private void m_jDelTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jDelTicketActionPerformed
        
        int res = JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.wannadelete"), AppLocal.getIntString("title.editor"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res == JOptionPane.YES_OPTION) {
            deleteTicket();
        }
        
    }//GEN-LAST:event_m_jDelTicketActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton m_jDelTicket;
    // End of variables declaration//GEN-END:variables
    
}
