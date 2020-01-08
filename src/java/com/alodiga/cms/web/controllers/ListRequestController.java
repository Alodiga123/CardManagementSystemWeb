package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.RequestEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.custom.components.ListcellEditButton;
import com.alodiga.cms.web.custom.components.ListcellViewButton;
import com.alodiga.cms.web.generic.controllers.GenericAbstractListController;
import static com.alodiga.cms.web.generic.controllers.GenericDistributionController.request;
import com.alodiga.cms.web.utils.Utils;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.Request;
import com.cms.commons.models.RequestType;
import com.cms.commons.models.User;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.text.SimpleDateFormat;
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

public class ListRequestController extends GenericAbstractListController<Request> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtRequestNumber;
    private RequestEJB requestEJB = null;
    private List<Request> requests = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize(); 
    }

    public void startListener() {
    }


    @Override
    public void initialize() {
        super.initialize();
        try {
            //Evaluar Permisos
            permissionEdit = true;
            permissionAdd = true; 
            permissionRead = true;
            adminPage = "TabNaturalPerson.zul";
            requestEJB = (RequestEJB) EJBServiceLocator.getInstance().get(EjbConstants.REQUEST_EJB);
            getData();
            loadList(requests);
        } catch (Exception ex) {
            showError(ex);
        }
    }
 
    public void onClick$btnAddNaturalPersonRequest() throws InterruptedException {
        Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_ADD);
        Executions.getCurrent().sendRedirect("TabNaturalPerson.zul");
    }
    
    public void onClick$btnAddLegalPersonRequest() throws InterruptedException {
        Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_ADD);
        Executions.getCurrent().sendRedirect("TabLegalPerson.zul");
    }

    public void onClick$btnDelete() {
    }

    public void loadList(List<Request> list) {
        String applicantName = "";
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                for (Request request : list) {
                    item = new Listitem();
                    item.setValue(request);
                    if (request.getPersonId() != null) {
                        if (request.getPersonId().getApplicantNaturalPerson() != null) {
                            applicantName = request.getPersonId().getApplicantNaturalPerson().getFirstNames();
                            applicantName.concat(" ");
                            applicantName.concat(request.getPersonId().getApplicantNaturalPerson().getLastNames());
                        } else {
                           applicantName = request.getPersonId().getLegalPerson().getEnterpriseName(); 
                        }   
                    } 
                    String pattern = "yyyy-MM-dd";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    item.appendChild(new Listcell(request.getRequestNumber()));
                    item.appendChild(new Listcell(simpleDateFormat.format(request.getRequestDate())));
                    item.appendChild(new Listcell(request.getRequestTypeId().getDescription()));
                    if (request.getPersonId() != null) {
                        item.appendChild(new Listcell(applicantName));
                    } else {
                        item.appendChild(new Listcell("SIN REGISTRAR"));
                    }
                    item.appendChild(new Listcell(request.getStatusRequestId().getDescription()));
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, request) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, request) : new Listcell());
                    item.setParent(lbxRecords);
                }
            } else {
                btnDownload.setVisible(false);
                item = new Listitem();
                item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.setParent(lbxRecords);
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void getData() {
        requests = new ArrayList<Request>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            requests = requestEJB.getRequests(request);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
           showEmptyList();
        } catch (GeneralException ex) {
            showError(ex);
        }
    }
    
    
    private void showEmptyList(){
                Listitem item = new Listitem();
                item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.setParent(lbxRecords);  
    }

    public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("cms.common.cardRequest.list"));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {
        txtRequestNumber.setText("");
    }

    @Override
    public List<Request> getFilterList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void loadDataList(List<Request> list) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
