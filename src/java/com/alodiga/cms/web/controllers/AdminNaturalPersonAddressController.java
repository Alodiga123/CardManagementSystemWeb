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
import com.cms.commons.models.City;
import com.cms.commons.models.Country;
import com.cms.commons.models.EdificationType;
import com.cms.commons.models.Person;
import com.cms.commons.models.PersonHasAddress;
import com.cms.commons.models.State;
import com.cms.commons.models.StreetType;
import com.cms.commons.models.ZipZone;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.cms.commons.util.QueryConstants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;

public class AdminNaturalPersonAddressController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtUbanization;
    private Textbox txtNameStreet;
    private Textbox txtNameEdification;
    private Textbox txtTower;
    private Textbox txtFloor;
    private Textbox txtEmail;
    private Combobox cmbCountry;
    private Combobox cmbState;
    private Combobox cmbCity;
    private Combobox cmbStreetType;
    private Combobox cmbEdificationType;
    private Combobox cmbZipZone;
    private Tab tabFamilyReferencesMain;
    private PersonEJB personEJB = null;
    private UtilsEJB utilsEJB = null;
    private Address addressParam;
    private Button btnSave;
    private Integer eventType;
    private AdminRequestController adminRequest = null;
    Map params = null;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        AdminRequestController adminRequest = new AdminRequestController();
        AdminNaturalPersonController adminPerson = new AdminNaturalPersonController();
        if (adminRequest.getEventType() != null) {
           eventType = adminRequest.getEventType();
           switch (eventType) {
                case WebConstants.EVENT_EDIT:
                    if (adminPerson.getApplicant().getPersonHasAddress() != null) {
                        addressParam = adminPerson.getApplicant().getPersonHasAddress().getAddressId();
                    } else {
                        addressParam = null;
                    }
                break;
                case WebConstants.EVENT_VIEW:
                    if (adminPerson.getApplicant().getPersonHasAddress() != null) {
                        addressParam = adminPerson.getApplicant().getPersonHasAddress().getAddressId();
                    } else {
                        addressParam = null;
                    }
                break;
                case WebConstants.EVENT_ADD:
                    addressParam = null;
                break;
            }
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
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
        txtEmail.setRawValue(null);
    }

    private void loadFields(Address address) {
        try {
            txtUbanization.setValue(address.getUrbanization());
            txtNameStreet.setValue(address.getNameStreet());
            txtNameEdification.setValue(address.getNameEdification());
            txtTower.setValue(address.getTower());
            txtFloor.setValue(address.getFloor().toString());

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
        txtEmail.setReadonly(true);
        cmbCountry.setReadonly(true);
        cmbState.setReadonly(true);
        cmbCity.setReadonly(true);
        cmbEdificationType.setReadonly(true);
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

    private void saveAddress(Address _address) {
        Person applicantCard = null;
        try {
            Address address = null;
            PersonHasAddress personHasAddress = null;

            if (_address != null) {
                address = _address;
            } else {//New address
                address = new Address();
                personHasAddress = new PersonHasAddress();
            }
            
            //Se obtiene la persona asociada al solicitante de tarjeta
            AdminNaturalPersonController adminNaturalPerson = new AdminNaturalPersonController();
            if (adminNaturalPerson.getApplicant().getId() != null) {
                applicantCard = adminNaturalPerson.getApplicant();
            }
            
            //Guarda la dirección del solicitante
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
            address = utilsEJB.saveAddress(address);
            addressParam = address;
            
            //Asocia la dirección al solicitante y la guarda en BD
            personHasAddress.setAddressId(address);
            personHasAddress.setPersonId(applicantCard);
            personHasAddress = personEJB.savePersonHasAddress(personHasAddress);
            
            this.showMessage("sp.common.save.success", false, null);
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
                    saveAddress(addressParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadCmbCountry(eventType);
                if (addressParam != null) {
                    loadFields(addressParam);
                    onChange$cmbCountry();
                    onChange$cmbState();
                    onChange$cmbCity();
                }
                LoadCmbStreetType(eventType);
                loadCmbEdificationType(eventType);
            break;
            case WebConstants.EVENT_VIEW:
                loadCmbCountry(eventType);
                if (addressParam != null) {
                    loadFields(addressParam);
                    onChange$cmbCountry();
                    onChange$cmbState();
                    onChange$cmbCity();
                    blockFields();
                }
                LoadCmbStreetType(eventType);
                loadCmbEdificationType(eventType);
            break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                LoadCmbStreetType(eventType);
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
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(addressParam != null ? addressParam.getCountryId().getId() : 0));
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
            loadGenericCombobox(states, cmbState, "name", evenInteger, Long.valueOf(addressParam != null ? addressParam.getCityId().getStateId().getId() : 0));
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
            loadGenericCombobox(citys, cmbCity, "name", evenInteger, Long.valueOf(addressParam != null ? addressParam.getCityId().getId() : 0));
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

    private void LoadCmbStreetType(Integer evenInteger) {
        //cmbStreetType
        EJBRequest request1 = new EJBRequest();
        List<StreetType> streetTypes;
        try {
            streetTypes = utilsEJB.getStreetTypes(request1);
            loadGenericCombobox(streetTypes, cmbStreetType, "description", evenInteger, Long.valueOf(addressParam != null ? addressParam.getStreetTypeId().getId() : 0));
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
            loadGenericCombobox(edificationTypes, cmbEdificationType, "description", evenInteger, Long.valueOf(addressParam != null ? addressParam.getEdificationTypeId().getId() : 0));
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
                if (addressParam != null && c.getId().equals(addressParam.getZipZoneId().getId())) {
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
