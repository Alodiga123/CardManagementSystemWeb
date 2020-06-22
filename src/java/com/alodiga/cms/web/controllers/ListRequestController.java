package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.RequestEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.commons.exception.RegisterNotFoundException;
import com.alodiga.cms.web.custom.components.ListcellEditButton;
import com.alodiga.cms.web.custom.components.ListcellViewButton;
import com.alodiga.cms.web.generic.controllers.GenericAbstractListController;
import static com.alodiga.cms.web.generic.controllers.GenericDistributionController.request;
import com.alodiga.cms.web.utils.Utils;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.Request;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public static int indAddRequestPerson;
    public static int indRequestOption = 1;

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
            Sessions.getCurrent().setAttribute(WebConstants.OPTION_MENU, indRequestOption);
            requestEJB = (RequestEJB) EJBServiceLocator.getInstance().get(EjbConstants.REQUEST_EJB);
            getData();
            loadList(requests);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public int getAddRequestPerson() {
        return indAddRequestPerson;
    }
    
    public int getIndRequestOption() {
        return indRequestOption;
    }

    public void onClick$btnAddNaturalPersonRequest() throws InterruptedException {
        Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_ADD);
        Executions.getCurrent().sendRedirect("TabNaturalPerson.zul");
        indAddRequestPerson = 1;
    }

    public void onClick$btnAddLegalPersonRequest() throws InterruptedException {
        Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_ADD);
        Executions.getCurrent().sendRedirect("TabLegalPerson.zul");
        indAddRequestPerson = 2;
    }

    public void onClick$btnDelete() {
    }

    public void loadList(List<Request> list) {
        String applicantNameLegal = "";
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                for (Request request : list) {
                    item = new Listitem();
                    item.setValue(request);
                    String pattern = "yyyy-MM-dd";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    item.appendChild(new Listcell(request.getRequestNumber()));
                    item.appendChild(new Listcell(simpleDateFormat.format(request.getRequestDate())));
                    item.appendChild(new Listcell(request.getStatusRequestId().getDescription()));
                    if (request.getPersonId() != null) {                        
                        if (request.getIndPersonNaturalRequest() == true) {
                            item.appendChild(new Listcell(request.getPersonTypeId().getDescription()));
                            StringBuilder applicantNameNatural = new StringBuilder(request.getPersonId().getApplicantNaturalPerson().getFirstNames());
                            applicantNameNatural.append(" ");
                            applicantNameNatural.append(request.getPersonId().getApplicantNaturalPerson().getLastNames());          
                            item.appendChild(new Listcell(applicantNameNatural.toString()));
                            adminPage = "TabNaturalPerson.zul";
                            item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, request) : new Listcell());
                            item.appendChild(permissionRead ? new ListcellViewButton(adminPage, request) : new Listcell());
                        } else {
                            item.appendChild(new Listcell(request.getPersonTypeId().getDescription()));
                            applicantNameLegal = request.getPersonId().getLegalPerson().getEnterpriseName();
                            item.appendChild(new Listcell(applicantNameLegal));
                            adminPage = "TabLegalPerson.zul";
                            item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, request) : new Listcell());
                            item.appendChild(permissionRead ? new ListcellViewButton(adminPage, request) : new Listcell());
                        }                                                
                    } else {                      
                        if (request.getIndPersonNaturalRequest() == true) {
                            item.appendChild(new Listcell(request.getPersonTypeId().getDescription()));
                            item.appendChild(new Listcell("SIN REGISTRAR"));
                            adminPage = "TabNaturalPerson.zul";
                            item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, request) : new Listcell());
                            item.appendChild(permissionRead ? new ListcellViewButton(adminPage, request) : new Listcell());
                        } else {
                            item.appendChild(new Listcell(request.getPersonTypeId().getDescription()));
                            item.appendChild(new Listcell("SIN REGISTRAR"));
                            adminPage = "TabLegalPerson.zul";
                            item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, request) : new Listcell());
                            item.appendChild(permissionRead ? new ListcellViewButton(adminPage, request) : new Listcell());
                        }
                    }
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

    private void showEmptyList() {
        Listitem item = new Listitem();
        item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.setParent(lbxRecords);
    }

    public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("cms.common.request.list"));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {
        txtRequestNumber.setText("");
    }

    public void onClick$btnSearch() throws InterruptedException {
        try {
            loadList(getFilterList(txtRequestNumber.getText()));
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    @Override
    public List<Request> getFilterList(String filter) {
        List<Request> requestaux = new ArrayList<Request>();
        Request request;
        try {
            if (filter != null && !filter.equals("")) {
                request = requestEJB.searchCardRequest(filter);
                requestaux.add(request);
            } else {
                return requests;
            }
        } catch (RegisterNotFoundException ex) {
            Logger.getLogger(ListRequestController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            showError(ex);
        }
        return requestaux;
    }

    @Override
    public void loadDataList(List<Request> list) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
