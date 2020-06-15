package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.custom.components.ListcellEditButton;
import com.alodiga.cms.web.custom.components.ListcellViewButton;
import com.alodiga.cms.web.generic.controllers.GenericAbstractListController;
import com.alodiga.cms.web.utils.Utils;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.PasswordChangeRequest;
import com.cms.commons.models.User;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.ArrayList;
import java.util.List;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

public class ListPasswordChangeRequestController extends GenericAbstractListController<PasswordChangeRequest> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private PersonEJB personEJB = null;
    private List<User> userList = null;
    private List<PasswordChangeRequest> passwordChangeRequestList = null;
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
            //Evaluar Permisos
            permissionEdit = true;
            permissionAdd = true;
            permissionRead = true;
            currentUser = (User) session.getAttribute(Constants.USER_OBJ_SESSION);
            adminPage = "adminPasswordChangeRequest.zul";
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            getData();
            loadDataList(passwordChangeRequestList);
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
   public void getData() {
    passwordChangeRequestList = new ArrayList<PasswordChangeRequest>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            passwordChangeRequestList = personEJB.getPasswordChangeRequest(request);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
        } catch (GeneralException ex) {
            showError(ex);
        }
    }



    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute("eventType", WebConstants.EVENT_ADD);
        Sessions.getCurrent().removeAttribute("object");
        Executions.getCurrent().sendRedirect(adminPage);
    }
    
       
   public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("sp.crud.enterprise.list"));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void startListener() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void loadDataList(List<PasswordChangeRequest> list) {
        String indApproved = null;
        Listitem item = null;
        try {
            lbxRecords.getItems().clear();
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (PasswordChangeRequest passwordChangeRequest : list) {
                    item = new Listitem();
                    item.setValue(passwordChangeRequest);
                    item.appendChild(new Listcell(passwordChangeRequest.getRequestNumber()));
                    item.appendChild(new Listcell(passwordChangeRequest.getRequestDate().toString()));
                    item.appendChild(new Listcell(passwordChangeRequest.getUserId().getCode().toString()));
                    StringBuilder userName = new StringBuilder(passwordChangeRequest.getUserId().getFirstNames());
                    userName.append(" ");
                    userName.append(passwordChangeRequest.getUserId().getLastNames());
                    item.appendChild(new Listcell(userName.toString()));
                    if (passwordChangeRequest.getIndApproved() == true) {
                        indApproved = "Yes";
                    } else {
                        indApproved = "No";
                    }
                    item.appendChild(new Listcell(indApproved));
                    item.appendChild(new ListcellEditButton(adminPage, passwordChangeRequest));
                    item.appendChild(new ListcellViewButton(adminPage, passwordChangeRequest,true));
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
                item.setParent(lbxRecords);
            }
        } catch (Exception ex) {
           showError(ex);
        }
    }

    @Override
    public List<PasswordChangeRequest> getFilterList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
