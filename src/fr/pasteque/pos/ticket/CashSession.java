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

import fr.pasteque.format.DateUtils;

import java.util.Date;
import org.json.JSONObject;

public class CashSession {

    private String id;
    private int cashRegisterId;
    private int sequence;
    private Date openDate;
    private Date closeDate;
    private Double openCash;
    private Double closeCash;
    private Double expectedCash;

    public CashSession(String id, int cashRegisterId, int sequence,
            Date openDate, Date closeDate, Double openCash, Double closeCash,
            Double expectedCash) {
        this.id = id;
        this.cashRegisterId = cashRegisterId;
        this.sequence = sequence;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.openCash = openCash;
        this.closeCash = closeCash;
        this.expectedCash = expectedCash;
    }

    public CashSession(JSONObject o) {
        this.id = o.getString("id");
        this.cashRegisterId = o.getInt("cashRegisterId");
        this.sequence = o.getInt("sequence");
        if (!o.isNull("openDate")) {
            this.openDate = DateUtils.readSecTimestamp(o.getLong("openDate"));
        }
        if (!o.isNull("closeDate")) {
            this.closeDate = DateUtils.readSecTimestamp(o.getLong("closeDate"));
        }
        if (!o.isNull("openCash")) {
            this.openCash = o.getDouble("openCash");
        }
        if (!o.isNull("closeCash")) {
            this.closeCash = o.getDouble("closeCash");
        }
        if (!o.isNull("expectedCash")) {
            this.expectedCash = o.getDouble("expectedCash");
        }
    }

    public JSONObject toJSON() {
        JSONObject o = new JSONObject();
        o.put("id", this.id);
        o.put("cashRegisterId", this.cashRegisterId);
        o.put("sequence", this.sequence);
        if (this.openDate == null) {
            o.put("openDate", JSONObject.NULL);
        } else {
            o.put("openDate", DateUtils.toSecTimestamp(this.openDate));
        }
        if (this.closeDate == null) {
            o.put("closeDate", JSONObject.NULL);
        } else {
            o.put("closeDate", DateUtils.toSecTimestamp(this.closeDate));
        }
        if (this.openCash == null) {
            o.put("openCash", JSONObject.NULL);
        } else {
            o.put("openCash", this.openCash);
        }
        if (this.closeCash == null) {
            o.put("closeCash", JSONObject.NULL);
        } else {
            o.put("closeCash", this.closeCash);
        }
        if (this.expectedCash == null) {
            o.put("expectedCash", JSONObject.NULL);
        } else {
            o.put("expectedCash", this.expectedCash);
        }
        return o;
    }

    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public int getCashRegisterId() {
        return this.cashRegisterId;
    }

    public int getSequence() {
        return this.sequence;
    }
    public void setSequence(int seq) {
        this.sequence = seq;
    }

    public Date getOpenDate() {
        return this.openDate;
    }

    public void open(Date d) {
        this.openDate = d;
    }

    public Date getCloseDate() {
        return this.closeDate;
    }

    public void close(Date d) {
        this.closeDate = d;
    }

    /** Cash is opened when usable (opened and not closed) */
    public boolean isOpened() {
        return this.openDate != null && this.closeDate == null;
    }

    public boolean wasOpened() {
        return this.openDate != null;
    }

    public boolean isClosed() {
        return this.closeDate == null;
    }

    public Double getOpenCash() {
        return this.openCash;
    }

    public void setOpenCash(Double amount) {
        this.openCash = amount;
    }

    public Double getCloseCash() {
        return this.closeCash;
    }

    public void setCloseCash(Double amount) {
        this.closeCash = amount;
    }

    public Double getExpectedCash() {
        return this.expectedCash;
    }

    public void setExpectedCash(Double amount) {
        this.expectedCash = amount;
    }
}