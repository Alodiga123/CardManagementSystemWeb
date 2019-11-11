package com.alodiga.cms.web.controllers;

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
import com.cms.commons.models.StatusRequest;
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

public class ListStatusRequestController extends GenericAbstractListController<StatusRequest> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtDescription;
    private UtilsEJB utilsEJB = null;
    private List<StatusRequest> statusRequest = null;
    //private User currentUser;
    //private Profile currentProfile;

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
            //
            
            adminPage = "adminStatusRequest.zul";
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            getData();
            loadList(statusRequest);
        } catch (Exception ex) {
            showError(ex);
        }
    }

//    public List<StatusRequest> getFilteredList(String filter) {
//        List<StatusRequest> statusRequestsaux = new ArrayList<StatusRequest>();
//        StatusRequest statusRequest;
//        try {
//            if (filter != null && !filter.equals("")) {
//                statusRequests = utilsEJB.searchStatusRequest(filter);
//                statusRequestsaux.add(statusRequest);
//            } else {
//                return statusRequests;
//            }
//        } catch (RegisterNotFoundException ex) {
//            Logger.getLogger(ListStatusRequestController.class.getDescription()).log(Level.SEVERE, null, ex);
//        } catch (Exception ex) {
//            showError(ex);
//        }
//        return statusRequestsaux;
//   }

    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_ADD);
        Executions.getCurrent().sendRedirect(adminPage);
    }

    public void onClick$btnDelete() {
    }

    public void loadList(List<StatusRequest> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
//                btnDownload.setVisible(true);
                for (StatusRequest statusRequest : list) {
                   
                    item = new Listitem();
                    item.setValue(statusRequest);
                    item.appendChild(new Listcell(statusRequest.getDescription()));
                    
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, statusRequest) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, statusRequest) : new Listcell());
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
        statusRequest = new ArrayList<StatusRequest>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            statusRequest = utilsEJB.getStatusRequests(request);
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

//    public String getCountryTraslationAlias(List<CountryTranslation> countryTranslations, Long languageId) {
//        String alias = "";
//        if (countryTranslations != null) {
//            for (CountryTranslation countryTranslation : countryTranslations) {
//                if (countryTranslation.getLanguage().getId().equals(languageId)) {
//                    alias = countryTranslation.getAlias();
//                }
//            }
//        }
//        return alias;
//    }

    public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("sp.bread.crumb.statusRequest.list"));
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

    @Override
    public List<StatusRequest> getFilterList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void loadDataList(List<StatusRequest> list) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
