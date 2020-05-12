package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.commons.exception.RegisterNotFoundException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Channel;
import com.cms.commons.models.ProgramLoyalty;
import com.cms.commons.models.ProgramLoyaltyTransaction;
import com.cms.commons.models.Transaction;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class AdminParametersController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;

    private Label lblLoyalty;
    private Label lblTitle;
    private Combobox cmbChannel;
    private Combobox cmbTransaction;
    private Doublebox txtTotal;
    private Doublebox txtTotalMaximumTransactions;
    private Doublebox txtTotalAmountDaily;
    private Doublebox txtTotalAmountMonthly;
    private Radio rBonificationYes;
    private Radio rBonificationNo;
    private Tab tabCommerce;
    private ProductEJB productEJB = null;
    private ProgramEJB programEJB = null;
    private ProgramLoyaltyTransaction programLoyaltyTransactionParam;
    private Button btnSave;
    private Integer eventType;
    public static ProgramLoyaltyTransaction programLoyaltyTransactionParent = null;
    public Window winAdminParameters;
    public Window winTabParametersAndComerce;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                programLoyaltyTransactionParam = (ProgramLoyaltyTransaction) Sessions.getCurrent().getAttribute("object");
                if (programLoyaltyTransactionParam.getTransactionId().getIndTransactionPurchase() != null) {
                    tabCommerce.setDisabled(false);
                } else {
                    tabCommerce.setDisabled(true);
                }
                break;
            case WebConstants.EVENT_VIEW:
                programLoyaltyTransactionParam = (ProgramLoyaltyTransaction) Sessions.getCurrent().getAttribute("object");
                if (programLoyaltyTransactionParam.getTransactionId().getIndTransactionPurchase() != null) {
                    tabCommerce.setDisabled(false);
                } else {
                    tabCommerce.setDisabled(true);
                }
                break;
            case WebConstants.EVENT_ADD:
                programLoyaltyTransactionParam = null;
                tabCommerce.setDisabled(true);
                break;
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                winTabParametersAndComerce.setTitle(Labels.getLabel("cms.crud.loyalty.commerceCategory.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                winTabParametersAndComerce.setTitle(Labels.getLabel("cms.crud.loyalty.commerceCategory.view"));
                break;
            case WebConstants.EVENT_ADD:
                winTabParametersAndComerce.setTitle(Labels.getLabel("cms.crud.loyalty.commerceCategory.add"));
                break;
            default:
                break;
        }
        try {
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            programEJB = (ProgramEJB) EJBServiceLocator.getInstance().get(EjbConstants.PROGRAM_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtTotal.setRawValue(null);
        txtTotalMaximumTransactions.setRawValue(null);
        txtTotalAmountDaily.setRawValue(null);
        txtTotalAmountMonthly.setRawValue(null);
    }

    public void onClick$rBonificationYes() {
        txtTotalMaximumTransactions.setDisabled(false);
        txtTotalAmountDaily.setDisabled(false);
        txtTotalAmountMonthly.setDisabled(false);
    }

    public void onClick$rBonificationNo() {
        txtTotalMaximumTransactions.setDisabled(true);
        txtTotalAmountDaily.setDisabled(false);
        txtTotalAmountMonthly.setDisabled(false);
    }

    public void onChange$cmbTransaction() {
        String indMonetaryTypeTrue = WebConstants.ID_MONETARY_TYPE_TRUE;
        String indMonetaryTypeFalse = WebConstants.ID_MONETARY_TYPE_FALSE;
        String cadena1 = (((Transaction) cmbTransaction.getSelectedItem().getValue()).getIndMonetaryType().toString());
        String cadena2 = (((Transaction) cmbTransaction.getSelectedItem().getValue()).getIndTransactionPurchase().toString());
        String cadena3 = (((Transaction) cmbTransaction.getSelectedItem().getValue()).getIndVariationRateChannel().toString());

        ProgramLoyalty programLoyalty = null;
        AdminLoyaltyController adminLoyalty = new AdminLoyaltyController();
        if (adminLoyalty.getProgramLoyaltyParent().getId() != null) {
            programLoyalty = adminLoyalty.getProgramLoyaltyParent();
        }

        if (cadena1.equals(indMonetaryTypeTrue)) {

        } else if (cadena1.equals(indMonetaryTypeFalse)) {
            if (programLoyalty.getProgramLoyaltyTypeId().getId() == WebConstants.PROGRAM_LOYALTY_TYPE_POINT) {
                lblTitle.setValue(Labels.getLabel("cms.crud.loyalty.parameters.totalPoint"));
                txtTotal.setDisabled(false);
                txtTotalAmountDaily.setDisabled(true);
                txtTotalAmountMonthly.setDisabled(true);
                txtTotalMaximumTransactions.setDisabled(true);
            } else if (programLoyalty.getProgramLoyaltyTypeId().getId() == WebConstants.PROGRAM_LOYALTY_TYPE_BONIFICATION) {
                lblTitle.setValue(Labels.getLabel("cms.crud.loyalty.parameters.totalBonification"));
                txtTotal.setDisabled(false);
                txtTotalAmountDaily.setDisabled(true);
                txtTotalAmountMonthly.setDisabled(true);
                txtTotalMaximumTransactions.setDisabled(true);
            }
        }
    }

    public ProgramLoyaltyTransaction getProgramLoyaltyTransactionParent() {
        return programLoyaltyTransactionParent;
    }

    private void loadField(ProgramLoyaltyTransaction programLoyaltyTransaction) {

        ProgramLoyalty programLoyalty = null;
        AdminLoyaltyController adminLoyalty = new AdminLoyaltyController();
        if (adminLoyalty.getProgramLoyaltyParent().getId() != null) {
            programLoyalty = adminLoyalty.getProgramLoyaltyParent();
        }
        lblLoyalty.setValue(programLoyalty.getDescription());

        if (programLoyaltyTransactionParam != null) {
            txtTotalMaximumTransactions.setText(programLoyaltyTransaction.getTotalMaximumTransactions().toString());
            txtTotalAmountDaily.setText(programLoyaltyTransaction.getTotalAmountDaily().toString());
            txtTotalAmountMonthly.setText(programLoyaltyTransaction.getTotalAmountMonthly().toString());

            if (programLoyaltyTransaction.getTransactionId().getIndTransactionPurchase() == true) {
                tabCommerce.setDisabled(false);
            } else {
                tabCommerce.setDisabled(true);
            }

            if (programLoyalty.getProgramLoyaltyTypeId().getId() == WebConstants.PROGRAM_LOYALTY_TYPE_POINT) {
                lblTitle.setValue(Labels.getLabel("cms.crud.loyalty.parameters.totalPoint"));
                txtTotal.setText(programLoyaltyTransaction.getTotalPointsValue().toString());
                rBonificationYes.setDisabled(true);
                rBonificationNo.setDisabled(true);
            } else if (programLoyalty.getProgramLoyaltyTypeId().getId() == WebConstants.PROGRAM_LOYALTY_TYPE_BONIFICATION) {
                lblTitle.setValue(Labels.getLabel("cms.crud.loyalty.parameters.totalBonification"));
                txtTotal.setText(programLoyaltyTransaction.getTotalBonificationValue().toString());
                if (programLoyaltyTransaction.getIndBonificationFixed() == true) {
                    rBonificationYes.setChecked(true);
                } else {
                    rBonificationNo.setChecked(true);
                }
            }
            programLoyaltyTransactionParent = programLoyaltyTransaction;
        } else {
            if (programLoyalty.getProgramLoyaltyTypeId().getId() == WebConstants.PROGRAM_LOYALTY_TYPE_POINT) {
                lblTitle.setValue(Labels.getLabel("cms.crud.loyalty.parameters.totalPoint"));
                rBonificationYes.setDisabled(true);
                rBonificationNo.setDisabled(true);
            } else if (programLoyalty.getProgramLoyaltyTypeId().getId() == WebConstants.PROGRAM_LOYALTY_TYPE_BONIFICATION) {
                lblTitle.setValue(Labels.getLabel("cms.crud.loyalty.parameters.totalBonification"));
                rBonificationYes.setDisabled(false);
                rBonificationNo.setDisabled(false);
            }
        }
        btnSave.setVisible(true);
    }

    public void blockFields() {
        txtTotal.setReadonly(true);
        txtTotalMaximumTransactions.setReadonly(true);
        txtTotalAmountDaily.setReadonly(true);
        txtTotalAmountMonthly.setReadonly(true);

        btnSave.setVisible(false);
    }

    private void saveProgramLoyaltyTransactionParam(ProgramLoyaltyTransaction _programLoyaltyTransaction) throws RegisterNotFoundException, NullParameterException, GeneralException {
        ProgramLoyaltyTransaction programLoyaltyTransaction = null;
        List<ProgramLoyaltyTransaction> programLoyaltyTransactionUnique = null;
        ProgramLoyalty programLoyalty = null;

        try {
            if (_programLoyaltyTransaction != null) {
                programLoyaltyTransaction = _programLoyaltyTransaction;
            } else {
                programLoyaltyTransaction = new ProgramLoyaltyTransaction();
            }

            //Loyalty
            AdminLoyaltyController adminLoyalty = new AdminLoyaltyController();
            if (adminLoyalty.getProgramLoyaltyParent().getId() != null) {
                programLoyalty = adminLoyalty.getProgramLoyaltyParent();
            }

            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.CHANNEL_KEY, ((Channel) cmbChannel.getSelectedItem().getValue()).getId());
            params.put(Constants.PROGRAM_LOYALTY_KEY, programLoyalty.getId());
            params.put(Constants.TRANSACTION_KEY, ((Transaction) cmbTransaction.getSelectedItem().getValue()).getId());
            request1.setParams(params);

            programLoyaltyTransactionUnique = programEJB.getProgramLoyaltyTransactionUnique(request1);
            if (programLoyaltyTransactionUnique != null) {
                switch (eventType) {
                    case WebConstants.EVENT_ADD:
                        this.showMessage("cms.common.programLoyaltyTransactionExist", false, null);
                    break;
                    case WebConstants.EVENT_EDIT:
                        buildProgramLoyaltyTransaction(programLoyalty,programLoyaltyTransaction);
                        programLoyaltyTransaction = programEJB.saveProgramLoyaltyTransaction(programLoyaltyTransaction);
                        programLoyaltyTransactionParam = programLoyaltyTransaction;
                        programLoyaltyTransactionParent = programLoyaltyTransaction;
                        this.showMessage("sp.common.save.success", false, null);
                    break;
                    default:
                    break;
                }
            }
            btnSave.setVisible(false);

        } catch (Exception ex) {
            showError(ex);
        } finally {
            if (eventType == 1 && programLoyaltyTransactionUnique == null) {
                programLoyaltyTransaction = new ProgramLoyaltyTransaction();
                buildProgramLoyaltyTransaction(programLoyalty,programLoyaltyTransaction);
                programLoyaltyTransaction = programEJB.saveProgramLoyaltyTransaction(programLoyaltyTransaction);
                programLoyaltyTransactionParam = programLoyaltyTransaction;
                programLoyaltyTransactionParent = programLoyaltyTransaction;
                this.showMessage("sp.common.save.success", false, null);
                if (programLoyaltyTransactionParam.getTransactionId().getIndTransactionPurchase() == true) {
                    tabCommerce.setDisabled(false);
                } else {
                    tabCommerce.setDisabled(true);
                }
            }
            btnSave.setVisible(false);
            EventQueues.lookup("updateParameters", EventQueues.APPLICATION, true).publish(new Event(""));
        }
    }
    
    public void buildProgramLoyaltyTransaction(ProgramLoyalty programLoyalty, ProgramLoyaltyTransaction programLoyaltyTransaction) {
        boolean IndBonificationFixed = true;
        
        if (rBonificationYes.isChecked()) {
                IndBonificationFixed = true;
            } else {
                IndBonificationFixed = false;
            }
        
        programLoyaltyTransaction.setChannelId((Channel) cmbChannel.getSelectedItem().getValue());
        programLoyaltyTransaction.setProgramLoyaltyId(programLoyalty);
        programLoyaltyTransaction.setTransactionId((Transaction) cmbTransaction.getSelectedItem().getValue());
        if (programLoyalty.getProgramLoyaltyTypeId().getId() == WebConstants.PROGRAM_LOYALTY_TYPE_POINT) {
            programLoyaltyTransaction.setTotalPointsValue(txtTotal.getValue().floatValue());
        } else if (programLoyalty.getProgramLoyaltyTypeId().getId() == WebConstants.PROGRAM_LOYALTY_TYPE_BONIFICATION) {
            programLoyaltyTransaction.setTotalBonificationValue(txtTotal.getValue().floatValue());
        }
        programLoyaltyTransaction.setTotalMaximumTransactions(txtTotalMaximumTransactions.getValue().floatValue());
        programLoyaltyTransaction.setTotalAmountDaily(txtTotalAmountDaily.getValue().floatValue());
        programLoyaltyTransaction.setTotalAmountMonthly(txtTotalAmountMonthly.getValue().floatValue());
        programLoyaltyTransaction.setIndBonificationFixed(IndBonificationFixed);
    }

    public void onClick$btnSave() throws RegisterNotFoundException, NullParameterException, GeneralException {
        switch (eventType) {
            case WebConstants.EVENT_ADD:
                saveProgramLoyaltyTransactionParam(null);
                break;
            case WebConstants.EVENT_EDIT:
                saveProgramLoyaltyTransactionParam(programLoyaltyTransactionParam);
                break;
            default:
                break;
        }
    }

    public void onClick$btnBack() {
        winAdminParameters.detach();
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadField(programLoyaltyTransactionParam);
                loadCmbTransaction(WebConstants.EVENT_VIEW);
                loadCmbChannel(WebConstants.EVENT_VIEW);
                break;
            case WebConstants.EVENT_VIEW:
                loadField(programLoyaltyTransactionParam);
                blockFields();
                loadCmbTransaction(eventType);
                loadCmbChannel(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadField(programLoyaltyTransactionParam);
                loadCmbTransaction(eventType);
                loadCmbChannel(eventType);
                break;
            default:
                break;
        }
    }

    private void loadCmbTransaction(Integer evenInteger) {
        //cmbTransaction
        EJBRequest request1 = new EJBRequest();
        List<Transaction> transactions;
        try {
            transactions = productEJB.getTransaction(request1);
            loadGenericCombobox(transactions, cmbTransaction, "description", evenInteger, Long.valueOf(programLoyaltyTransactionParam != null ? programLoyaltyTransactionParam.getTransactionId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }

    private void loadCmbChannel(Integer evenInteger) {
        //cmbChannel
        EJBRequest request1 = new EJBRequest();
        List<Channel> channels;
        try {
            channels = productEJB.getChannel(request1);
            loadGenericCombobox(channels, cmbChannel, "name", evenInteger, Long.valueOf(programLoyaltyTransactionParam != null ? programLoyaltyTransactionParam.getChannelId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }
}
