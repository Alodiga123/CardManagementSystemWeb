package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.CardEJB;
import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.commons.exception.RegisterNotFoundException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.ApprovalCardRate;
import com.cms.commons.models.ApprovalProductRate;
import com.cms.commons.models.Card;
import com.cms.commons.models.Program;
import com.cms.commons.models.RateByCard;
import com.cms.commons.models.RateByProduct;
import com.cms.commons.models.User;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.cms.commons.util.QueryConstants;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Window;

public class AdminApprovalCardRateController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblProgram;
    private Label txtCity;
    private Label txtAgency;
    private Label txtCommercialAssessorUserCode;
    private Label txtAssessorName;
    private Label txtIdentification;
    private Datebox txtApprovalDate;
    private Radio rApprovedYes;
    private Radio rApprovedNo;
    private ProductEJB productEJB = null;
    private CardEJB cardEJB = null;
    private User user = null;
    private ApprovalCardRate approvalCardRateParam;
    private Button btnSave;
    public Window winAdminApprovalCardRate;
    private Program program;
    private List<RateByCard> rateByCardByProductList = new ArrayList<RateByCard>();
    private Card card;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            approvalCardRateParam = null;                    
        } else {
            approvalCardRateParam = (ApprovalCardRate) Sessions.getCurrent().getAttribute("object");            
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            user = (User) session.getAttribute(Constants.USER_OBJ_SESSION);
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            cardEJB = (CardEJB) EJBServiceLocator.getInstance().get(EjbConstants.CARD_EJB);
            card = (Card) session.getAttribute(WebConstants.CARD);
            loadData();
            this.clearMessage();
        } catch (Exception ex) {
            showError(ex);
        } finally {
            loadData();
        }
    }

    public void clearFields() {
        txtApprovalDate.setRawValue(null);
    }

    private void loadFields(ApprovalCardRate approvalCardRate) throws EmptyListException, GeneralException, NullParameterException {
        try {
            program = (Program) session.getAttribute(WebConstants.PROGRAM);
            lblProgram.setValue(program.getName());
            txtCity.setValue(approvalCardRate.getUserId().getComercialAgencyId().getCityId().getName());
            txtAgency.setValue(approvalCardRate.getUserId().getComercialAgencyId().getName());
            txtCommercialAssessorUserCode.setValue(approvalCardRate.getUserId().getCode());
            txtAssessorName.setValue(approvalCardRate.getUserId().getFirstNames() + " " + approvalCardRate.getUserId().getLastNames());
            txtIdentification.setValue(approvalCardRate.getUserId().getIdentificationNumber());
            txtApprovalDate.setValue(approvalCardRate.getApprovalDate());
            if (approvalCardRate.getIndApproved() == true) {
                rApprovedYes.setChecked(true);    
            } else {
                rApprovedNo.setChecked(true);
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtApprovalDate.setDisabled(true);
        rApprovedYes.setDisabled(true);
        rApprovedNo.setDisabled(true);        
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtApprovalDate.getText().isEmpty()) {
            txtApprovalDate.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;
    }

    private void saveApprovalRates(ApprovalCardRate _approvalCardRate) {
        ApprovalCardRate approvalCardRate = null;
        boolean indApproved;
        try {
            if (_approvalCardRate != null) {
                approvalCardRate = _approvalCardRate;
            } else {
                approvalCardRate = new ApprovalCardRate();
            }
            
            if (rApprovedYes.isChecked()) {
                indApproved = true;
            } else {
                indApproved = false;
            }
            
            //Guarda la aprobación de las tarifas por programa
            approvalCardRate.setCardId(card);
            approvalCardRate.setApprovalDate(txtApprovalDate.getValue());
            approvalCardRate.setIndApproved(indApproved);
            approvalCardRate.setUserId(user);
            approvalCardRate.setCreateDate(new Timestamp(new Date().getTime()));
            approvalCardRate = productEJB.saveApprovalCardRate(approvalCardRate);
            
            //Actualiza las tarifas del programa que se está aprobando
            updateCardRate(approvalCardRate);
            
            this.showMessage("sp.common.save.success", false, null);
            EventQueues.lookup("updateApprovalProductRate", EventQueues.APPLICATION, true).publish(new Event(""));
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public void updateCardRate(ApprovalCardRate approvalCardRate) {
        try {
            Map params = new HashMap();
            EJBRequest request1 = new EJBRequest();
            params.put(QueryConstants.PARAM_CARD_ID, card.getId());
            request1.setParams(params);
            rateByCardByProductList = cardEJB.getRateByCardByCard(request1);
            for (RateByCard rateByCard: rateByCardByProductList) {
                rateByCard.setApprovalCardRateId(approvalCardRate);
                rateByCard = cardEJB.saveRateByCard(rateByCard);
            }
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
           showError(ex); 
        } catch (GeneralException ex) {
            showError(ex);
        } catch (RegisterNotFoundException ex) {
            showError(ex); 
        }           
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveApprovalRates(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveApprovalRates(approvalCardRateParam);
                    break;
                case WebConstants.EVENT_VIEW:
                    blockFields();
                    break;
                default:
                    break;
            }
        }
    }
    
    public void onClick$btnBack() {
        winAdminApprovalCardRate.detach();
    }

    public void loadData() {
        try {
            switch (eventType) {
                case WebConstants.EVENT_EDIT:
                    loadFields(approvalCardRateParam);
                break;
                case WebConstants.EVENT_VIEW:
                    loadFields(approvalCardRateParam);
                    blockFields();
                break;
                case WebConstants.EVENT_ADD:
                    lblProgram.setValue(program.getName());
                    txtCity.setValue(user.getComercialAgencyId().getCityId().getName());
                    txtAgency.setValue(user.getComercialAgencyId().getName());
                    txtCommercialAssessorUserCode.setValue(user.getCode());
                    txtAssessorName.setValue(user.getFirstNames() + " " + user.getLastNames());
                    txtIdentification.setValue(user.getIdentificationNumber());
                    blockFields();
                break;
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