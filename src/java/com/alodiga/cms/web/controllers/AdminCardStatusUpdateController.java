package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.CardEJB;
import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Card;
import com.cms.commons.models.CardStatus;
import com.cms.commons.models.CardStatusHasUpdateReason;
import com.cms.commons.models.LegalCustomer;
import com.cms.commons.models.NaturalCustomer;
import com.cms.commons.models.Profile;
import com.cms.commons.models.StatusUpdateReason;
import com.cms.commons.models.User;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.api.Textbox;

public class AdminCardStatusUpdateController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblCarHolder;
    private Label lblCardNumber;
    private Label lblTypeProduct;
    private Label lblIdentification;
    private Label lblStatus;
    private Label lblExpirationDate;
    private Label lblDateOfIssue;
    private Combobox cmbCardStatus;
    private Combobox cmbStatusUpdateReason;

    private Label lblUser;
    private Label lblCity;
    private Label lblIdentificationCardHolder;
    private Label lblComercial;
    private Textbox txtReason;
    
    private UtilsEJB utilsEJB = null;
    private CardEJB cardEJB = null;
    private PersonEJB personEJB = null;
    private ProgramEJB programEJB = null;
    private ProductEJB productEJB = null;
    private Card cardParam;
    private Button btnSave;
    private Integer evenType;
    private boolean NotStatusUpdateReasonId;
    private  User user;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        evenType = (Integer) (Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE));
        if (eventType != WebConstants.EVENT_ADD) {
            cardParam = (Card) Sessions.getCurrent().getAttribute("object");
            if (cardParam.getStatusUpdateReasonId() == null) {
                NotStatusUpdateReasonId = false; 
            }
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            cardEJB = (CardEJB) EJBServiceLocator.getInstance().get(EjbConstants.CARD_EJB);
            programEJB = (ProgramEJB) EJBServiceLocator.getInstance().get(EjbConstants.PROGRAM_EJB);
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);

            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
    }

    private void loadFields(Card card) {
        try {
            
            user = (User) session.getAttribute(Constants.USER_OBJ_SESSION);
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            NaturalCustomer naturalCustomer;
            LegalCustomer legalCustomer;
            
            switch(card.getPersonCustomerId().getPersonTypeId().getId()){
                case WebConstants.NATURAL_CUSTOMER:
                    naturalCustomer = getCardHolderNaturalInfo(card.getPersonCustomerId().getId());
                    lblIdentificationCardHolder.setValue(naturalCustomer.getIdentificationNumber());
                    break;
                case WebConstants.LEGAL_CUSTOMER:
                    legalCustomer = getCardHolderLegalInfo(card.getPersonCustomerId().getId());
                    lblIdentificationCardHolder.setValue(legalCustomer.getIdentificationNumber());
                    break;
                default:
                    naturalCustomer = getCardHolderNaturalInfo(card.getPersonCustomerId().getId());
                    lblIdentificationCardHolder.setValue(naturalCustomer.getIdentificationNumber());
            }
            
            lblCarHolder.setValue(card.getCardHolder());
            lblTypeProduct.setValue(card.getProgramId().getProductTypeId().getName());
            lblDateOfIssue.setValue(simpleDateFormat.format(card.getIssueDate()));
            lblComercial.setValue(user.getComercialAgencyId().getName());
            lblUser.setValue(user.getLogin());
            lblCity.setValue(user.getComercialAgencyId().getCityId().getName());
            lblIdentification.setValue(user.getIdentificationNumber());  
            
            
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        cmbCardStatus.setDisabled(true);
        cmbStatusUpdateReason.setDisabled(true);
        txtReason.setReadonly(true);
        txtReason.setDisabled(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        
        if (cmbStatusUpdateReason.getSelectedItem() == null || cmbCardStatus.getSelectedItem() == null
                || txtReason.getText() == null) 
        {
         this.showMessage("sp.common.field.required.full", true, null);
         return false;
         
        } else {
            StatusUpdateReason statusUpdateReason = (StatusUpdateReason) cmbStatusUpdateReason.getSelectedItem().getValue();
            CardStatusHasUpdateReason cardStatusHasUpdateReason = (CardStatusHasUpdateReason) cmbCardStatus.getSelectedItem().getValue();
            CardStatus cardStatus= cardStatusHasUpdateReason.getCardStatusId();
            cardParam.setStatusUpdateReasonDate(new Timestamp(new Date().getTime()));
            cardParam.setUserResponsibleStatusUpdateId(user);
            cardParam.setStatusUpdateReasonId(statusUpdateReason);
            cardParam.setCardStatusId(cardStatus);
            cardParam.setObservations(txtReason.getText());
            return true;
        }
        
        
    }

    private void saveCardStatus(Card _card) {
        try {
            Card card = null;

            if (_card != null) {
                card = _card;
            } else {//New requestType
                card = new Card();
            }
            Card result = result= cardEJB.saveCard(card);
            if(card!=null){
               this.showMessage("sp.common.save.success", false, null);
                blockFields();
            }else{
            this.showMessage("cms.msj.errorUpdateCard", true, null);
            }
            
        } catch (Exception ex) {
            this.showMessage("cms.msj.errorUpdateCard", true, null);

            showError(ex);
            ex.printStackTrace();
        }

    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (evenType) {
                case WebConstants.EVENT_ADD:
                    //saveCardStatus(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveCardStatus(cardParam);
                    break;
            }
        }
    }

    public void loadData() {
        switch (evenType) {
            case WebConstants.EVENT_EDIT:
                loadFields(cardParam);
                loadCmbStatusUpdateReason(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(cardParam);
                loadCmbStatusUpdateReason(eventType);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }
    
    
    public LegalCustomer getCardHolderLegalInfo(long personId){
        List<LegalCustomer> legalCustomerList = null;
       
        Map params = new HashMap();
        params.put(Constants.PERSON_KEY, personId);
        EJBRequest request1 = new EJBRequest();
        request1.setParams(params);
        LegalCustomer legalCustomer = null;            
        try {
            legalCustomerList = personEJB.getLegalCustomerByPerson(request1);          
        } catch (EmptyListException ex) {
            Logger.getLogger(AdminCardStatusUpdateController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralException ex) {
            Logger.getLogger(AdminCardStatusUpdateController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullParameterException ex) {
            Logger.getLogger(AdminCardStatusUpdateController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        for (LegalCustomer n : legalCustomerList) {
            legalCustomer = n;
        }    
        return  legalCustomer;
    }
    
     public NaturalCustomer getCardHolderNaturalInfo(long personId){    
        List<NaturalCustomer> naturalCustomerList = null;
        Map params = new HashMap();
        params.put(Constants.PERSON_KEY, personId);
        EJBRequest request1 = new EJBRequest();
        request1.setParams(params);
        NaturalCustomer naturalCustomer = null;
            
        try {         
            naturalCustomerList = personEJB.getNaturalCustomerByPerson(request1);   
           
            for (NaturalCustomer n : naturalCustomerList) {
                    naturalCustomer = n;
            }
        } catch (EmptyListException ex) {
            Logger.getLogger(AdminCardStatusUpdateController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralException ex) {
            Logger.getLogger(AdminCardStatusUpdateController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullParameterException ex) {
            Logger.getLogger(AdminCardStatusUpdateController.class.getName()).log(Level.SEVERE, null, ex);
        }     
        return  naturalCustomer;
    }

    private void loadCmbStatusUpdateReason(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<StatusUpdateReason> statusUpdateReason;
        try {
            statusUpdateReason = cardEJB.getStatusUpdateReason(request1);
            loadGenericCombobox(statusUpdateReason, cmbStatusUpdateReason, "description", evenInteger,  Long.valueOf(NotStatusUpdateReasonId == true ? cardParam.getStatusUpdateReasonId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }
     
         
    public void onChange$cmbStatusUpdateReason() {
        cmbCardStatus.setValue("");
        StatusUpdateReason statusUpdateReason = (StatusUpdateReason) cmbStatusUpdateReason.getSelectedItem().getValue();
        loadCmbCardStatus(eventType,statusUpdateReason.getId());
    }     
     
    private void loadCmbCardStatus(Integer evenInteger, int statusUpdateReasonId) {  
        cmbCardStatus.getItems().clear();
        EJBRequest request1 = new EJBRequest();
        Map params = new HashMap();
        params.put(Constants.STATUS_UPDATE_REASON_KEY, statusUpdateReasonId);
        request1.setParams(params);                    
        List<CardStatusHasUpdateReason> cardStatusList;
        try {
            cardStatusList = cardEJB.getCardStatusByUpdateReason(request1);
            for (int i = 0; i < cardStatusList.size(); i++) {
                Comboitem item = new Comboitem();
                item.setValue(cardStatusList.get(i));
                item.setLabel(cardStatusList.get(i).getCardStatusId().getDescription());
                item.setParent(cmbCardStatus);
                if (cardParam != null && cardStatusList.get(i).getId().equals(cardParam.getCardStatusId().getId())) {
                    cmbCardStatus.setSelectedItem(item);
                }
            }
            if (evenInteger.equals(WebConstants.EVENT_VIEW)) {
                cmbCardStatus.setDisabled(true);
            }   
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }
}
