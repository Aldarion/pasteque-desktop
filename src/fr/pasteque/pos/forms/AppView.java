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

import java.util.Date;
import fr.pasteque.pos.printer.*;
import fr.pasteque.pos.scale.DeviceScale;
import fr.pasteque.pos.scanpal2.DeviceScanner;
import fr.pasteque.pos.ticket.CashRegisterInfo;
import fr.pasteque.pos.ticket.CashSession;

/**
 *
 * @author adrianromero
 */
public interface AppView {
    
    public DeviceScale getDeviceScale();
    public DeviceTicket getDeviceTicket();
    public DeviceScanner getDeviceScanner();
      
    public AppProperties getProperties();
    public Object getBean(String beanfactory) throws BeanFactoryException;

    public void newActiveCash();
    public void setActiveCash(CashSession cashSess);
    public CashSession getActiveCashSession();
    public CashRegisterInfo getCashRegister();
    public String getActiveCashIndex();
    public int getActiveCashSequence();
    public Date getActiveCashDateStart();
    
    public String getInventoryLocation();
    
    public void waitCursorBegin();
    public void waitCursorEnd();
    
    public AppUserView getAppUserView();
}

