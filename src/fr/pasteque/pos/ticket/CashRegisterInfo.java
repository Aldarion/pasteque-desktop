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

package fr.pasteque.pos.ticket;

import org.json.JSONObject;

/** Model for Cash register config. */
public class CashRegisterInfo {

    private int id;
    private String label;
    private String locationId;
    private int nextTicketId;

    public CashRegisterInfo(String label, String locationId, int nextTicketId) {
        this.label = label;
        this.locationId = locationId;
        this.nextTicketId = nextTicketId;
    }

    public CashRegisterInfo(JSONObject o) {
        this.id = o.getInt("id");
        this.label = o.getString("label");
        this.locationId = o.getString("locationId");
        this.nextTicketId = o.getInt("nextTicketId");
    }

    public int getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public String getLocationId() {
        return this.locationId;
    }

    public int getNextTicketId() {
        return this.nextTicketId;
    }

    public void incrementNextTicketId() {
        this.nextTicketId++;
    }
}