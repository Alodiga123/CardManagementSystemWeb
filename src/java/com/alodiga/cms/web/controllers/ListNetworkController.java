package com.alodiga.cms.web.controllers;
import com.alodiga.cms.commons.ejb.UtilsEJB;
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
import com.cms.commons.models.Network;
import com.cms.commons.models.State;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
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

public class ListNetworkController extends GenericAbstractListController<Network> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtName;
    private UtilsEJB utilsEJB = null;
    private List<Network> network = null;

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
            adminPage = "adminNetwork.zul";
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            getData();
            loadList(network);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_ADD);
        Executions.getCurrent().sendRedirect(adminPage);
    }

    public void onClick$btnDelete() {
    }

    public void loadList(List<Network> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
             
                for (Network network : list) {
                    item = new Listitem();
                    item.setValue(network);
                    item.appendChild(new Listcell(network.getName().toString()));
                    item.appendChild(new Listcell(network.getCountryId().getName()));               
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, network) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, network) : new Listcell());
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
        network = new ArrayList<Network>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            network = utilsEJB.getNetworks(request);
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
            Utils.exportExcel(lbxRecords, Labels.getLabel("cms.menu.network.list"));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {
        txtName.setText("");
    }

    public void onClick$btnSearch() throws InterruptedException {
        try {
            loadList(getFilterList(txtName.getText()));
        } catch (Exception ex) {
            showError(ex);
        }
    }
    @Override
    public List<Network> getFilterList(String filter) {
         List<Network> networkaux = new ArrayList<Network>();
        Network networks;
        try {
            if (filter != null && !filter.equals("")) {
                networks = utilsEJB.searchNetwork(filter);
                networkaux.add(networks);
            } else {
                return network; 
            }
        } catch (RegisterNotFoundException ex) {
            Logger.getLogger(ListNetworkController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            showError(ex);
        }
        return networkaux;
    }

    @Override
    public void loadDataList(List<Network> list) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

  
    

}
