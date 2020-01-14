package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.ejb.RequestEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.City;
import com.cms.commons.models.Country;
import com.cms.commons.models.Product;
import com.cms.commons.models.ReviewCollectionsRequest;
import com.cms.commons.models.State;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.cms.commons.util.QueryConstants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Textbox;

public class AdminApplicationReviewController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Datebox txtReviewDate;
    private Textbox txtMaximumRechargeAmount;
    private Textbox txtObservations;
    private Combobox cmbCountry;
    private Combobox cmbState;
    private Combobox cmbCity;
    private Combobox cmbProduct;
    private UtilsEJB utilsEJB = null;
    private ProductEJB productEJB = null;
    private RequestEJB requestEJB = null;
    private ReviewCollectionsRequest reviewCollectionsRequestParam;
    private Button btnSave;
    private Integer eventType;
    private AdminRequestController adminRequest = null;
    Map params = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        AdminRequestController adminRequest = new AdminRequestController();
        AdminNaturalPersonController adminPerson = new AdminNaturalPersonController();
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            requestEJB = (RequestEJB) EJBServiceLocator.getInstance().get(EjbConstants.REQUEST_EJB);
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

    public void clearFields() {
        txtReviewDate.setRawValue(null);
        txtMaximumRechargeAmount.setRawValue(null);
        txtObservations.setRawValue(null);
    }

    private void loadFields(ReviewCollectionsRequest reviewCollectionsRequest) {
        try {
            txtReviewDate.setValue(reviewCollectionsRequest.getReviewDate());
            //txtMaximumRechargeAmount.setValue(reviewCollectionsRequest.getMaximumRechargeAmount());
            //txtObservations.setValue(reviewCollectionsRequest.get);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtReviewDate.setReadonly(true);
        txtMaximumRechargeAmount.setReadonly(true);
        cmbCountry.setReadonly(true);
        cmbState.setReadonly(true);
        cmbCity.setReadonly(true);
        cmbProduct.setReadonly(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtReviewDate.getText().isEmpty()) {
            txtReviewDate.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtMaximumRechargeAmount.getText().isEmpty()) {
            txtMaximumRechargeAmount.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtObservations.getText().isEmpty()) {
            txtObservations.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;
    }

    private void saveAddress(ReviewCollectionsRequest _reviewCollectionsRequest) {
        AdminRequestController adminRequest = new AdminRequestController();
        try {
            ReviewCollectionsRequest reviewCollectionsRequest = null;

            if (_reviewCollectionsRequest != null) {
                reviewCollectionsRequest = _reviewCollectionsRequest;
            } else {//New address
                reviewCollectionsRequest = new ReviewCollectionsRequest();
            }

//            //Se obtiene la persona asociada al solicitante de tarjeta
//            AdminRequestController adminRequest = new AdminRequestController();
//            if (adminRequest.getRequest().getId() != null) {
//                requestNumber = adminRequest.getRequest();
//            }

            //Guarda la dirección del solicitante
            reviewCollectionsRequest.setRequestId(adminRequest.getRequest());
            reviewCollectionsRequest.setReviewDate(txtReviewDate.getValue());
            reviewCollectionsRequest.setMaximumRechargeAmount(Float.parseFloat(txtMaximumRechargeAmount.getText()));
            //reviewCollectionsRequest.setUserId(User);
            reviewCollectionsRequest.setProductId((Product) cmbProduct.getSelectedItem().getValue());
            //reviewCollectionsRequest.setObservations(txtObservations.getText());
            reviewCollectionsRequest = requestEJB.saveReviewCollectionsRequest(reviewCollectionsRequest);

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
                    saveAddress(reviewCollectionsRequestParam);
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
                loadFields(reviewCollectionsRequestParam);
                onChange$cmbCountry();
                onChange$cmbState();
                loadCmbProduct(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadCmbCountry(eventType);
                loadFields(reviewCollectionsRequestParam);
                onChange$cmbCountry();
                onChange$cmbState();
                blockFields();
                loadCmbProduct(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                loadCmbProduct(eventType);
                break;
        }
    }

    private void loadCmbCountry(Integer evenInteger) {
        //cmbCountry
        EJBRequest request1 = new EJBRequest();
        List<Country> countries;
        try {
            countries = utilsEJB.getCountries(request1);
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(reviewCollectionsRequestParam != null ? reviewCollectionsRequestParam.getUserId().getComercialAgencyId().getCityId().getStateId().getCountryId().getId() : 0));
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
            loadGenericCombobox(states, cmbState, "name", evenInteger, Long.valueOf(reviewCollectionsRequestParam != null ? reviewCollectionsRequestParam.getUserId().getComercialAgencyId().getCityId().getStateId().getId() : 0));
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
            loadGenericCombobox(citys, cmbCity, "name", evenInteger, Long.valueOf(reviewCollectionsRequestParam != null ? reviewCollectionsRequestParam.getUserId().getComercialAgencyId().getCityId().getId() : 0));
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

    private void loadCmbProduct(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Product> product;
        try {
            product = productEJB.getProduct(request1);
            loadGenericCombobox(product, cmbProduct, "name", evenInteger, Long.valueOf(reviewCollectionsRequestParam != null ? reviewCollectionsRequestParam.getProductId().getId() : 0));
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
