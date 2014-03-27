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

package fr.pasteque.pos.sales;

import fr.pasteque.basic.BasicException;
import fr.pasteque.data.loader.DataRead;
import fr.pasteque.data.loader.Datas;
import fr.pasteque.data.loader.ImageLoader;
import fr.pasteque.data.loader.SerializerRead;
import fr.pasteque.data.loader.SerializerReadString;
import fr.pasteque.data.loader.SerializerWriteBasic;
import fr.pasteque.data.loader.SerializerWriteString;
import fr.pasteque.pos.forms.AppLocal;
import fr.pasteque.pos.inventory.AttributeSetInfo;
import fr.pasteque.pos.widgets.JEditorKeys;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.swing.SwingUtilities;

/**
 *
 * @author adrianromero
 */
public class JProductAttEdit extends javax.swing.JDialog {

    private List<JProductAttEditI> itemslist;
    private String attsetid;
    private String attInstanceId;
    private String attInstanceDescription;

    private boolean ok;

    /** Creates new form JProductAttEdit */
    private JProductAttEdit(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }

    /** Creates new form JProductAttEdit */
    private JProductAttEdit(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
    }

    private void init() {

        initComponents();
        /*
        attsetSave = new PreparedSentence(s,
                "INSERT INTO ATTRIBUTESETINSTANCE (ID, ATTRIBUTESET_ID, DESCRIPTION) VALUES (?, ?, ?)",
                new SerializerWriteBasic(Datas.STRING, Datas.STRING, Datas.STRING));
        attinstSave = new PreparedSentence(s,
                "INSERT INTO ATTRIBUTEINSTANCE(ID, ATTRIBUTESETINSTANCE_ID, ATTRIBUTE_ID, VALUE) VALUES (?, ?, ?, ?)",
                new SerializerWriteBasic(Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING));

        attsetSent = new PreparedSentence(s,
                "SELECT ID, NAME FROM ATTRIBUTESET WHERE ID = ?",
                SerializerWriteString.INSTANCE,
                new SerializerRead() { public Object readValues(DataRead dr) throws BasicException {
                    return new AttributeSetInfo(dr.getString(1), dr.getString(2));
                }});
        attsetinstExistsSent = new PreparedSentence(s,
                "SELECT ID FROM ATTRIBUTESETINSTANCE WHERE ATTRIBUTESET_ID = ? AND DESCRIPTION = ?",
                new SerializerWriteBasic(Datas.STRING, Datas.STRING),
                SerializerReadString.INSTANCE);

        attinstSent = new PreparedSentence(s, "SELECT A.ID, A.NAME, " + s.DB.CHAR_NULL() + ", " + s.DB.CHAR_NULL() + " " +
                "FROM ATTRIBUTEUSE AU JOIN ATTRIBUTE A ON AU.ATTRIBUTE_ID = A.ID " +
                "WHERE AU.ATTRIBUTESET_ID = ? " +
                "ORDER BY AU.LINENO",
            SerializerWriteString.INSTANCE,
            new SerializerRead() { public Object readValues(DataRead dr) throws BasicException {
                return new AttributeInstInfo(dr.getString(1), dr.getString(2), dr.getString(3), dr.getString(4));
            }});
        attinstSent2 = new PreparedSentence(s, "SELECT A.ID, A.NAME, AI.ID, AI.VALUE " +
            "FROM ATTRIBUTEUSE AU JOIN ATTRIBUTE A ON AU.ATTRIBUTE_ID = A.ID LEFT OUTER JOIN ATTRIBUTEINSTANCE AI ON AI.ATTRIBUTE_ID = A.ID " +
            "WHERE AU.ATTRIBUTESET_ID = ? AND AI.ATTRIBUTESETINSTANCE_ID = ?" +
            "ORDER BY AU.LINENO",
            new SerializerWriteBasic(Datas.STRING, Datas.STRING),
            new SerializerRead() { public Object readValues(DataRead dr) throws BasicException {
                return new AttributeInstInfo(dr.getString(1), dr.getString(2), dr.getString(3), dr.getString(4));
            }});
        attvaluesSent = new PreparedSentence(s, "SELECT VALUE FROM ATTRIBUTEVALUE WHERE ATTRIBUTE_ID = ?",
                SerializerWriteString.INSTANCE,
                SerializerReadString.INSTANCE);
        */
        getRootPane().setDefaultButton(m_jButtonOK);
    }


    public static JProductAttEdit getAttributesEditor(Component parent) {

        Window window = SwingUtilities.getWindowAncestor(parent);

        JProductAttEdit myMsg;
        if (window instanceof Frame) {
            myMsg = new JProductAttEdit((Frame) window, true);
        } else {
            myMsg = new JProductAttEdit((Dialog) window, true);
        }
        myMsg.init();
        myMsg.applyComponentOrientation(parent.getComponentOrientation());
        return myMsg;
    }

    public void editAttributes(String attsetid, String attsetinstid) throws BasicException {

        if (attsetid == null) {
            throw new BasicException(AppLocal.getIntString("message.cannotfindattributes"));
        } else {

            this.attsetid = attsetid;
            this.attInstanceId = null;
            this.attInstanceDescription = null;

            this.ok = false;

            // get attsetinst values
            AttributeSetInfo asi = null;//(AttributeSetInfo) attsetSent.find(attsetid);

            if (asi == null) {
                throw new BasicException(AppLocal.getIntString("message.cannotfindattributes"));
            }

            setTitle(asi.getName());

            List<AttributeInstInfo> attinstinfo = null;/*attsetinstid == null
                    ? attinstSent.list(attsetid)
                    : attinstSent2.list(attsetid, attsetinstid);*/
            if (attinstinfo.size() == 0) {
                throw new BasicException(AppLocal.getIntString("message.cannotfindattributes"));
            }
            itemslist = new ArrayList<JProductAttEditI>();

            for (AttributeInstInfo aii : attinstinfo) {

                JProductAttEditI item;

                List<String> values = null;//attvaluesSent.list(aii.getAttid());
                if (values == null || values.isEmpty()) {
                    // Does not exist a list of values then a textfield
                    item = new JProductAttEditItem(aii.getAttid(),  aii.getAttname(), aii.getValue(), m_jKeys);
                } else {
                    // Does exist a list with the values
                    item = new JProductAttListItem(aii.getAttid(),  aii.getAttname(), aii.getValue(), values);
                }

                itemslist.add(item);
                jPanel2.add(item.getComponent());
            }

            if (itemslist.size() > 0) {
                itemslist.get(0).assignSelection();
            }
        }
    }

    public boolean isOK() {
        return ok;
    }

    public String getAttributeSetInst() {
        return attInstanceId;
    }

    public String getAttributeSetInstDescription() {
        return attInstanceDescription;
    }

    private static class AttributeInstInfo {
        
        private String attid;
        private String attname;
        private String id;
        private String value;

        public AttributeInstInfo(String attid, String attname, String id, String value) {
            this.attid = attid;
            this.attname = attname;
            this.id = id;
            this.value = value;
        }

        /**
         * @return the attid
         */
        public String getAttid() {
            return attid;
        }

        /**
         * @return the attname
         */
        public String getAttname() {
            return attname;
        }

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id the id to set
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return the value
         */
        public String getValue() {
            return value;
        }

        /**
         * @param value the value to set
         */
        public void setValue(String value) {
            this.value = value;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        m_jButtonOK = new javax.swing.JButton();
        m_jButtonCancel = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        m_jKeys = new JEditorKeys();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.PAGE_AXIS));
        jPanel5.add(jPanel2, java.awt.BorderLayout.NORTH);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        m_jButtonOK.setIcon(ImageLoader.readImageIcon("button_ok.png"));
        m_jButtonOK.setText(AppLocal.getIntString("Button.OK")); // NOI18N
        m_jButtonOK.setFocusPainted(false);
        m_jButtonOK.setFocusable(false);
        m_jButtonOK.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jButtonOK.setRequestFocusEnabled(false);
        m_jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jButtonOKActionPerformed(evt);
            }
        });
        jPanel1.add(m_jButtonOK);

        m_jButtonCancel.setIcon(ImageLoader.readImageIcon("button_cancel.png"));
        m_jButtonCancel.setText(AppLocal.getIntString("Button.Cancel")); // NOI18N
        m_jButtonCancel.setFocusPainted(false);
        m_jButtonCancel.setFocusable(false);
        m_jButtonCancel.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jButtonCancel.setRequestFocusEnabled(false);
        m_jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jButtonCancelActionPerformed(evt);
            }
        });
        jPanel1.add(m_jButtonCancel);

        jPanel5.add(jPanel1, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel5, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.Y_AXIS));
        jPanel4.add(m_jKeys);

        jPanel3.add(jPanel4, java.awt.BorderLayout.NORTH);

        getContentPane().add(jPanel3, java.awt.BorderLayout.EAST);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-609)/2, (screenSize.height-388)/2, 609, 388);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jButtonOKActionPerformed
       
        StringBuilder description = new StringBuilder();
        for (JProductAttEditI item : itemslist) {
            String value = item.getValue();
            if (value != null && value.length() > 0) {
                if (description.length() > 0) {
                    description.append(", ");
                }
                description.append(value);
            }
        }



        String id = null;

        if (description.length() == 0) {
            // No values then id is null
            id = null;
        } else {
            // Some values then an instance should exists.
            /*try {
                // Exist an attribute set instance with these values for the attributeset selected
                id = (String) attsetinstExistsSent.find(attsetid, description.toString());
            } catch (BasicException ex) {
                // Logger.getLogger(JProductAttEdit.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }


            if (id == null) {
                // No, create a new ATTRIBUTESETINSTANCE and return the ID generated
                // or return null... That means that that product does not exists....
                // Maybe these two modes must be supported one for selection and other for creation....
                id = UUID.randomUUID().toString();
                try {
                    attsetSave.exec(id, attsetid, description.toString());
                    for (JProductAttEditI item : itemslist) {
                        attinstSave.exec(UUID.randomUUID().toString(), id, item.getAttribute(), item.getValue());
                    }

                } catch (BasicException ex) {
                    // Logger.getLogger(JProductAttEdit.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                    }
            }*/
        }

        ok = true;
        attInstanceId = id;
        attInstanceDescription = description.toString();

        dispose();
    }//GEN-LAST:event_m_jButtonOKActionPerformed

    private void m_jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jButtonCancelActionPerformed

        dispose();
    }//GEN-LAST:event_m_jButtonCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JButton m_jButtonCancel;
    private javax.swing.JButton m_jButtonOK;
    private JEditorKeys m_jKeys;
    // End of variables declaration//GEN-END:variables

}
