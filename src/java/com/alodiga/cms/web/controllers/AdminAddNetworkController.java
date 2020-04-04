package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Country;
import com.cms.commons.models.Network;
import com.cms.commons.models.Program;
import com.cms.commons.models.ProgramHasNetwork;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.cms.commons.util.QueryConstants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

public class AdminAddNetworkController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Combobox cmbCountry;
    private Combobox cmbNetwork;
    private UtilsEJB utilsEJB = null;
    private ProgramEJB programEJB = null;
    private ProgramHasNetwork programHasNetworksParam;
    private Button btnSave;
    public Window winAddNetwork;
    private Integer eventType;
    Map params = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                programHasNetworksParam = (ProgramHasNetwork) Sessions.getCurrent().getAttribute("object");
                break;
            case WebConstants.EVENT_VIEW:
                programHasNetworksParam = (ProgramHasNetwork) Sessions.getCurrent().getAttribute("object");
                break;
            case WebConstants.EVENT_ADD:
                programHasNetworksParam = null;
                break;
        }
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            programEJB = (ProgramEJB) EJBServiceLocator.getInstance().get(EjbConstants.PROGRAM_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onChange$cmbCountry() {
        cmbNetwork.setVisible(true);
        Country country = (Country) cmbCountry.getSelectedItem().getValue();
        loadCmbNetworks(eventType, country.getId());
    }
    
    public void onClick$btnAdd() {
        cmbCountry.setValue("");
        cmbNetwork.setValue("");
        this.clearMessage();
    }

    private void loadFields(ProgramHasNetwork programHasNetwork) {
    }

    public void blockFields() {
        cmbCountry.setDisabled(true);
        btnSave.setVisible(false);
    }

    private void saveProgramHasNetwork(ProgramHasNetwork _programHasNetwork) {
        Program program = null;
        Network network = null;
        ProgramHasNetwork programHasNetwork = null;
        List<ProgramHasNetwork> programHasNetworkList = null;
        int indExistRegister = 0;
        
        try {
            if (_programHasNetwork != null) {
                programHasNetwork = _programHasNetwork;
            } else {//New address
                programHasNetwork = new ProgramHasNetwork();
            }
            
            //Red asociada al programa
            network = (Network) cmbNetwork.getSelectedItem().getValue();
            
            //Programa
            AdminProgramController adminProgram = new AdminProgramController();
            if (adminProgram.getProgramParent().getId() != null) {
                program = adminProgram.getProgramParent();
            }
            
            //Verifica si el registro existe en BD
            if (eventType == WebConstants.EVENT_ADD) {
                EJBRequest request = new EJBRequest();
                params = new HashMap();
                params.put(Constants.PROGRAM_KEY, program.getId());
                params.put(Constants.NETWORK_KEY, network.getId());
                request.setParams(params);
                programHasNetworkList = programEJB.getProgramHasNetworkByNetworkByProgram(request);
                if (programHasNetworkList != null) {
                    indExistRegister = 1;
                    this.showMessage("cms.common.msj.registrationAlreadyExistsProgramHasNetwork", false, null);
                }
            }
        } catch (Exception ex) {
            showError(ex);
        } finally {
            try {
                if (indExistRegister == 0) {
                    //ProgramHasNetwork
                    programHasNetwork.setProgramId(program);
                    programHasNetwork.setNetworkId((Network) cmbNetwork.getSelectedItem().getValue());
                    programHasNetwork = programEJB.saveProgramHasNetwork(programHasNetwork);
                    programHasNetworksParam = programHasNetwork;
                    this.showMessage("sp.common.save.success", false, null);
                    EventQueues.lookup("updateNetwork", EventQueues.APPLICATION, true).publish(new Event(""));
                }                
            } catch (Exception ex) {
                showError(ex);   
            }
        }
    }
    
    public void onClick$btnSave() {
        switch (eventType) {
            case WebConstants.EVENT_ADD:
                saveProgramHasNetwork(null);
                break;
            case WebConstants.EVENT_EDIT:
                saveProgramHasNetwork(programHasNetworksParam);
                break;
            default:
                break;
        }
    }

    public void onClick$btnBack() {
        winAddNetwork.detach();
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(programHasNetworksParam);
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(programHasNetworksParam);
                blockFields();
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                break;
            default:
                break;
        }
    }

    private void loadCmbCountry(Integer evenInteger) {
        //cmbCountry
        EJBRequest request1 = new EJBRequest();
        List<Country> countries;
        try {
            countries = utilsEJB.getCountries(request1);
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(programHasNetworksParam != null ? programHasNetworksParam.getNetworkId().getCountryId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (GeneralException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (NullParameterException ex) {
            showError(ex);
            ex.printStackTrace();
        }
    }

    private void loadCmbNetworks(Integer evenInteger, int countryId) {
        //cmbState
        EJBRequest request1 = new EJBRequest();
        cmbNetwork.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_COUNTRY_ID, countryId);
        request1.setParams(params);
        List<Network> networks;
        try {
            networks = utilsEJB.getNetworkByCountry(request1);
            loadGenericCombobox(networks, cmbNetwork, "name", evenInteger, Long.valueOf(programHasNetworksParam != null ? programHasNetworksParam.getNetworkId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (GeneralException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (NullParameterException ex) {
            showError(ex);
            ex.printStackTrace();
        }
    }

    public void clearFields() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
