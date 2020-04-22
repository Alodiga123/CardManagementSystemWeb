package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Address;
import com.cms.commons.models.AddressType;
import com.cms.commons.models.City;
import com.cms.commons.models.Country;
import com.cms.commons.models.EdificationType;
import com.cms.commons.models.Person;
import com.cms.commons.models.PersonHasAddress;
import com.cms.commons.models.State;
import com.cms.commons.models.StreetType;
import com.cms.commons.models.ZipZone;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.cms.commons.util.QueryConstants;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class AdminAddressCardProgramManagerController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtUbanization;
    private Textbox txtNameStreet;
    private Textbox txtNameEdification;
    private Textbox txtTower;
    private Textbox txtFloor;
    private Combobox cmbCountry;
    private Combobox cmbState;
    private Combobox cmbCity;
    private Combobox cmbStreetType;
    private Combobox cmbEdificationType;
    private Combobox cmbZipZone;
    private PersonEJB personEJB = null;
    private UtilsEJB utilsEJB = null;
    public static Address addressParent = null;
    private PersonHasAddress personHasAddressParam;
    private Button btnSave;
    private Integer eventType;
    public Window winAdminAddressCardProgMan;
    private AdminCardProgramManagerController adminCardProgramManager = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp); 
        adminCardProgramManager = new AdminCardProgramManagerController(); 
        personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
        try {
            if (adminCardProgramManager.getEventType() == WebConstants.EVENT_ADD) {
                personHasAddressParam = null;
            } else {            
                if (adminCardProgramManager.getCardProgramManager().getPersonId() != null) {
                    EJBRequest request = new EJBRequest();
                    Map params = new HashMap();
                    params.put(Constants.PERSON_KEY,adminCardProgramManager.getCardProgramManager().getPersonId().getId());
                    request.setParams(params);
                    List<PersonHasAddress> personHasAddressList = personEJB.getPersonHasAddressesByPerson(request); 
                    for (PersonHasAddress p : personHasAddressList) {
                        personHasAddressParam = p;
                    }
                }          
            }                   
        } catch (Exception ex) {
            showError(ex);
        } finally {
            if (personHasAddressParam == null) {
                personHasAddressParam = null;
                eventType = WebConstants.EVENT_ADD;
            }
            initialize();
        }
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);            
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onChange$cmbCountry() {
        cmbState.setVisible(true);
        Country country = (Country) cmbCountry.getSelectedItem().getValue();
        loadCmbState(eventType, country.getId());
    }

    public void onChange$cmbState() {
        cmbCity.setVisible(true);
        State state = (State) cmbState.getSelectedItem().getValue();
        loadCmbCity(eventType, state.getId());
    }

    public void onChange$cmbCity() {
        cmbZipZone.setVisible(true);
        City city = (City) cmbCity.getSelectedItem().getValue();
        LoadCmbZipZone(eventType, city.getId());
    }

    public void clearFields() {
        txtUbanization.setRawValue(null);
        txtNameStreet.setRawValue(null);
        txtNameEdification.setRawValue(null);
        txtTower.setRawValue(null);
        txtFloor.setRawValue(null);
    }

    private void loadFields(PersonHasAddress personHasAddress) {
        try {
            txtUbanization.setValue(personHasAddress.getAddressId().getUrbanization());
            txtNameStreet.setValue(personHasAddress.getAddressId().getNameStreet());
            txtNameEdification.setValue(personHasAddress.getAddressId().getNameEdification());
            txtTower.setValue(personHasAddress.getAddressId().getTower());
            txtFloor.setValue(personHasAddress.getAddressId().getFloor().toString());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtUbanization.setReadonly(true);
        txtNameStreet.setReadonly(true);
        txtNameEdification.setReadonly(true);
        txtTower.setReadonly(true);
        txtFloor.setReadonly(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtUbanization.getText().isEmpty()) {
            txtUbanization.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtNameStreet.getText().isEmpty()) {
            txtNameStreet.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtNameEdification.getText().isEmpty()) {
            txtNameEdification.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtTower.getText().isEmpty()) {
            txtTower.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtFloor.getText().isEmpty()) {
            txtFloor.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;
    }

    private void saveAddress(PersonHasAddress _personHasAddress) {
        Person person = null;
        try {
            Address address = null;
            PersonHasAddress personHasAddress = null;
            person = adminCardProgramManager.getCardProgramManager().getPersonId();

            if (_personHasAddress != null) {
                address = _personHasAddress.getAddressId();
                personHasAddress = _personHasAddress;

            } else {//New address
                address = new Address();
                personHasAddress = new PersonHasAddress();
            }  
            
            //Obtiene el tipo de Direccion EMPRESA
            EJBRequest request = new EJBRequest();
            Map param = new HashMap();
            request.setParam(WebConstants.ADDRESS_TYPE_COMPANY);
            AddressType addressType = utilsEJB.loadAddressType(request);
            
            //Guarda la dirección del gerente del programa de tarjetas
            address.setEdificationTypeId((EdificationType) cmbEdificationType.getSelectedItem().getValue());
            address.setNameEdification(txtNameEdification.getText());
            address.setTower(txtTower.getText());
            address.setFloor(Integer.parseInt(txtFloor.getText()));
            address.setStreetTypeId((StreetType) cmbStreetType.getSelectedItem().getValue());
            address.setNameStreet(txtNameStreet.getText());
            address.setUrbanization(txtUbanization.getText());
            address.setCityId((City) cmbCity.getSelectedItem().getValue());
            address.setZipZoneId((ZipZone) cmbZipZone.getSelectedItem().getValue());
            address.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            address.setAddressTypeId(addressType);
            address.setCreateDate(new Timestamp(new Date().getTime()));
            address = utilsEJB.saveAddress(address);
            addressParent = address;

            //Asocia la dirección al solicitante y la guarda en BD
            personHasAddress.setAddressId(address);
            personHasAddress.setPersonId(person);
            personHasAddress = personEJB.savePersonHasAddress(personHasAddress);
            personHasAddressParam = personHasAddress;

            this.showMessage("sp.common.save.success", false, null);
            btnSave.setDisabled(true);

        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveAddress(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveAddress(personHasAddressParam);
                    break;
            }
        }
    }

    public void onClick$btnBack() {
        winAdminAddressCardProgMan.detach();
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadCmbCountry(eventType);
                if (personHasAddressParam != null) {
                    loadFields(personHasAddressParam);
                    onChange$cmbCountry();
                    onChange$cmbState();
                    onChange$cmbCity();
                }
                loadCmbStreetType(eventType);
                loadCmbEdificationType(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadCmbCountry(eventType);
                if (personHasAddressParam != null) {
                    loadFields(personHasAddressParam);
                    onChange$cmbCountry();
                    onChange$cmbState();
                    onChange$cmbCity();
                }
                loadCmbStreetType(eventType);
                loadCmbEdificationType(eventType);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                loadCmbStreetType(eventType);
                loadCmbEdificationType(eventType);
                break;
        }
    }

    private void loadCmbCountry(Integer evenInteger) {
        //cmbCountry
        EJBRequest request1 = new EJBRequest();
        List<Country> countries;
        try {
            countries = utilsEJB.getCountries(request1);
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(personHasAddressParam != null ? personHasAddressParam.getAddressId().getCountryId().getId() : 0));
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

    private void loadCmbState(Integer evenInteger, int countryId) {
        //cmbState
        EJBRequest request1 = new EJBRequest();
        cmbState.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_COUNTRY_ID, countryId);
        request1.setParams(params);
        List<State> states;
        try {
            states = utilsEJB.getStatesByCountry(request1);
            loadGenericCombobox(states, cmbState, "name", evenInteger, Long.valueOf(personHasAddressParam != null ? personHasAddressParam.getAddressId().getCityId().getStateId().getId() : 0));
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

    private void loadCmbCity(Integer evenInteger, int stateId) {
        //cmbCity
        EJBRequest request1 = new EJBRequest();
        cmbCity.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_STATE_ID, stateId);
        request1.setParams(params);
        List<City> citys;
        try {
            citys = utilsEJB.getCitiesByState(request1);
            loadGenericCombobox(citys, cmbCity, "name", evenInteger, Long.valueOf(personHasAddressParam != null ? personHasAddressParam.getAddressId().getCityId().getId() : 0));
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

    private void loadCmbStreetType(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<StreetType> streetTypes;
        try {
            streetTypes = utilsEJB.getStreetTypes(request1);
            loadGenericCombobox(streetTypes, cmbStreetType, "description", evenInteger, Long.valueOf(personHasAddressParam != null ? personHasAddressParam.getAddressId().getStreetTypeId().getId() : 0));
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

    private void loadCmbEdificationType(Integer evenInteger) {
        //cmbEdificationType
        EJBRequest request1 = new EJBRequest();
        List<EdificationType> edificationTypes;
        try {
            edificationTypes = utilsEJB.getEdificationTypes(request1);
            loadGenericCombobox(edificationTypes, cmbEdificationType, "description", evenInteger, Long.valueOf(personHasAddressParam != null ? personHasAddressParam.getAddressId().getEdificationTypeId().getId() : 0));
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

    private void LoadCmbZipZone(Integer evenInteger, int cityId) {
        //cmbZipZone
        EJBRequest request1 = new EJBRequest();
        cmbZipZone.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_CITY_ID, cityId);
        request1.setParams(params);
        List<ZipZone> zipZones;
        try {
            zipZones = utilsEJB.getZipZoneByCities(request1);
            cmbZipZone.getItems().clear();
            for (ZipZone c : zipZones) {
                Comboitem item = new Comboitem();
                item.setValue(c);
                item.setLabel(c.getCode());
                item.setDescription(c.getName());
                item.setParent(cmbZipZone);
                if (personHasAddressParam != null && c.getId().equals(personHasAddressParam.getAddressId().getZipZoneId().getId())) {
                    cmbZipZone.setSelectedItem(item);
                }
            }
            if (evenInteger.equals(WebConstants.EVENT_VIEW)) {
                cmbZipZone.setDisabled(true);
            }
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

}
