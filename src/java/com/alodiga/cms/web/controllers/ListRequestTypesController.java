package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.custom.components.ListcellEditButton;
import com.alodiga.cms.web.custom.components.ListcellViewButton;
import com.alodiga.cms.web.generic.controllers.GenericAbstractListController;
import com.alodiga.cms.web.utils.Utils;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.RequestType;
import com.cms.commons.models.User;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

public class ListRequestTypesController extends GenericAbstractListController<RequestType> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtName;
    private UtilsEJB utilsEJB = null;
    private List<RequestType> requestTypes = null;
    private User currentUser;
   

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }

    
    @Override
    public void initialize() {
        super.initialize();
        try {
            currentUser = (User) session.getAttribute(Constants.USER_OBJ_SESSION);
            adminPage = "adminRequestType.zul";
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            getData();
            loadDataList(requestTypes);
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
   public void getData() {
    requestTypes = new ArrayList<RequestType>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            requestTypes = utilsEJB.getRequestType(request);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
        } catch (GeneralException ex) {
            showError(ex);
        }
    }

    public List<RequestType> getFilteredList(String filter) {
        System.out.println("filter " + filter);
        List<RequestType> requestTypesAux = new ArrayList<RequestType>();
        for (Iterator<RequestType> i = requestTypes.iterator(); i.hasNext();) {
            RequestType tmp = i.next();
            String field = tmp.getProgramId().getName();
            if (field.indexOf(filter.trim().toLowerCase()) >= 0) {
                requestTypesAux.add(tmp);
            }
        }
        return requestTypesAux;
    }

    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute("eventType", WebConstants.EVENT_ADD);
        Sessions.getCurrent().removeAttribute("object");
        Executions.getCurrent().sendRedirect(adminPage);
    }
    
   public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("cms.crud.requestType.list"));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {
        txtName.setText("");
    }

    public void startListener() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<RequestType> getFilterList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void loadDataList(List<RequestType> list) {
          try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (RequestType requestType : list) {
                    item = new Listitem();
                    item.setValue(requestType);
                    item.appendChild(new Listcell(requestType.getProgramId().getName()));
                    item.appendChild(new Listcell(requestType.getProductTypeId().getName()));
                    item.appendChild(new Listcell(requestType.getCountryId().getName()));
                    item.appendChild(new Listcell(requestType.getPersonTypeId().getDescription()));
                    item.appendChild(new Listcell(requestType.getCardRequestTypeId().getDescription()));
                    item.appendChild( new ListcellEditButton("", requestType));
                    item.appendChild(new ListcellViewButton("", requestType,true));
                    item.setParent(lbxRecords);
                }
            } else {
                btnDownload.setVisible(false);
                item = new Listitem();
                item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.setParent(lbxRecords);
            }
        } catch (Exception ex) {
           showError(ex);
        }
    }

}
