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

package fr.pasteque.pos.sales;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import fr.pasteque.basic.BasicException;
import fr.pasteque.data.loader.Datas;
import fr.pasteque.data.loader.ServerLoader;
import fr.pasteque.data.loader.Session;
import fr.pasteque.pos.forms.BeanFactoryDataSingle;
import fr.pasteque.pos.ticket.TicketInfo;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 *
 * @author adrianromero
 */
public class DataLogicReceipts extends BeanFactoryDataSingle {
    
    private Session s;
    
    /** Creates a new instance of DataLogicReceipts */
    public DataLogicReceipts() {
    }
    
    public void init(Session s){
        this.s = s;
    }
     
    public final TicketInfo getSharedTicket(String id) throws BasicException {
        try {
            ServerLoader loader = new ServerLoader();
            ServerLoader.Response r = loader.read("TicketsAPI", "getShared",
                    "id", id);
            if (r.getStatus().equals(ServerLoader.Response.STATUS_OK)) {
                JSONObject o = r.getObjContent();
                if (o == null) {
                    return null;
                }
                String strdata = o.getString("data");
                byte[] data = DatatypeConverter.parseBase64Binary(strdata);
                TicketInfo tkt = new TicketInfo(data);
                return tkt;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BasicException(e);
        }
    } 
    
    public final List<SharedTicketInfo> getSharedTicketList() throws BasicException {
        List<SharedTicketInfo> tkts = new ArrayList<SharedTicketInfo>();
        try {
            ServerLoader loader = new ServerLoader();
            ServerLoader.Response r = loader.read("TicketsAPI", "getAllShared");
            if (r.getStatus().equals(ServerLoader.Response.STATUS_OK)) {
                JSONArray a = r.getArrayContent();
                for (int i = 0; i < a.length(); i++) {
                    JSONObject o = a.getJSONObject(i);
                    SharedTicketInfo stkt = new SharedTicketInfo(o);
                    tkts.add(stkt);
                }
                return tkts;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BasicException(e);
        }
    }
    
    public final void updateSharedTicket(final String id, final TicketInfo ticket) throws BasicException {
        try {
            ServerLoader loader = new ServerLoader();
            ServerLoader.Response r;
            JSONObject tkt = new JSONObject();
            tkt.put("id", id);
            tkt.put("label", ticket.getName());
            byte[] data = ticket.serialize();
            String strData = DatatypeConverter.printBase64Binary(data);
            tkt.put("data", strData);
            r = loader.write("TicketsAPI", "share",
                    "ticket", tkt.toString());
            if (!r.getStatus().equals(ServerLoader.Response.STATUS_OK)) {
                throw new BasicException("Bad server response");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BasicException(e);
        }
    }
    
    public final void insertSharedTicket(final String id, final TicketInfo ticket) throws BasicException {
        this.updateSharedTicket(id, ticket);
    }
    
    public final void deleteSharedTicket(final String id) throws BasicException {
        try {
            ServerLoader loader = new ServerLoader();
            ServerLoader.Response r;
            r = loader.write("TicketsAPI", "delShared", "id", id);
            if (!r.getStatus().equals(ServerLoader.Response.STATUS_OK)) {
                throw new BasicException("Bad server response");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BasicException(e);
        }
    }    
}
