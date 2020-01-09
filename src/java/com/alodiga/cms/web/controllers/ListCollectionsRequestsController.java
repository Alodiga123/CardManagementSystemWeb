package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.RequestEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.custom.components.ListcellEditButton;
import com.alodiga.cms.web.custom.components.ListcellViewButton;
import com.alodiga.cms.web.generic.controllers.GenericAbstractListController;
import static com.alodiga.cms.web.generic.controllers.GenericDistributionController.request;
import com.alodiga.cms.web.utils.Utils;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.CollectionsRequest;
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

public class ListCollectionsRequestsController extends GenericAbstractListController<CollectionsRequest> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtDescription;
    private RequestEJB requestEJB = null;
    private List<CollectionsRequest> collectionsRequest = null;

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
            adminPage = "adminCollectionsRequest.zul";
            requestEJB = (RequestEJB) EJBServiceLocator.getInstance().get(EjbConstants.REQUEST_EJB);
            getData();
            loadDataList(collectionsRequest);
        } catch (Exception ex) {
            showError(ex);
        }
    }

//    public List<CollectionsRequest> getFilteredList(String filter) {
//        List<CollectionsRequest> collectionsRequestaux = new ArrayList<CollectionsRequest>();
//        CollectionsRequest collectionsRequest;
//        try {
//            if (filter != null && !filter.equals("")) {
//                collectionsRequest = utilsEJB.searchCollectionsRequest(filter);
//                collectionsRequest.add(collectionsRequest);
//            } else {
//                return collectionsRequest;
//            }
//        } catch (RegisterNotFoundException ex) {
//            Logger.getLogger(ListCollectionsRequest.class.getDescription()).log(Level.SEVERE, null, ex);
//        } catch (Exception ex) {
//            showError(ex);
//        }
//        return collectionsRequest;
//   }

    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_ADD);
        Executions.getCurrent().sendRedirect(adminPage);
    }

    public void onClick$btnDelete() {
    }

    public void loadDataList(List<CollectionsRequest> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                for (CollectionsRequest collectionsRequest : list) {
                    item = new Listitem();
                    item.setValue(collectionsRequest);
                    item.appendChild(new Listcell(collectionsRequest.getCountryId().getName()));
                    item.appendChild(new Listcell(collectionsRequest.getPersonTypeId().getDescription()));
                    item.appendChild(new Listcell(collectionsRequest.getProductTypeId().getName()));
                    item.appendChild(new Listcell(collectionsRequest.getCollectionTypeId().getDescription()));
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, collectionsRequest) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, collectionsRequest) : new Listcell());
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
        collectionsRequest = new ArrayList<CollectionsRequest>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            collectionsRequest = requestEJB.getCollectionsRequests(request);
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
            Utils.exportExcel(lbxRecords, Labels.getLabel("sp.bread.crumb.collectionsRequest.list"));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {
        txtDescription.setText("");
    }

//    public void onClick$btnSearch() throws InterruptedException {
//        try {
//            loadList(getFilteredList(txtAlias.getText()));
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }

    public List<CollectionsRequest> getFilterList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
