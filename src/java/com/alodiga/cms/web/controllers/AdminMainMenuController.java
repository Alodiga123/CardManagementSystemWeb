package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.managers.PermissionManager;
import com.alodiga.cms.web.utils.AccessControl;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.Permission;
import com.cms.commons.models.PermissionGroup;
import com.cms.commons.models.User;
import com.cms.commons.util.Constants;
import java.util.ArrayList;
import java.util.List;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;

public class AdminMainMenuController extends GenericForwardComposer {

    private static final long serialVersionUID = -9145887024839938515L;
    User currentuser = null;
    Listcell ltcFullName;
    Listcell ltcProfile;
    Listcell ltcLogin;
    private static String OPTION = "option";
    private static String OPTION_NONE = "none";
    private static String OPTION_CUSTOMERS_LIST = "ltcCustomerList";
    private List<Permission> permissions;
    private List<PermissionGroup> permissionGroups;
    private List<PermissionGroup> pGroups;
    private PermissionManager pm = null;
    private Listbox lbxPermissions;
    private Long languageId;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }

    public void initialize() {
        try {
            pm = PermissionManager.getInstance();
            languageId = AccessControl.getLanguage();
            loadPemissions();
            loadAccountData();
            checkOption();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void checkOption() {

    }
    
        private void loadPemissions() {
        try {
            permissions = pm.getPermissions();
            if (permissions != null && !permissions.isEmpty()) {
                loadMenu();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private Permission loadPermission(Long permissionId) {
        for (Permission p : permissions) {
            if (p.getId().equals(permissionId)) {
                return p;
            }
        }
        return null;
    }
    
    private void loadMenu() {
        try {
            pGroups = new ArrayList<PermissionGroup>();
            permissionGroups = pm.getPermissionGroups();
            for (PermissionGroup pg : permissionGroups) {
                if (existPermissionInGroup(permissions, pg.getId())) {
                    pGroups.add(pg);
                }
            }

            if (!pGroups.isEmpty()) {
                for (PermissionGroup pg : pGroups) {
                    switch (pg.getId().intValue()) {
                        case 1://Secutiry Management
                            loadSecurityManagementGroup(pg);
                            break;
                        case 2://Basic Tables Management
                            loadBasicTableManagementGroup(pg);
                            break;
                        case 3://Programs Management
                            loadProgramsManagementGroup(pg);
                            break;
                        case 4://Card Request
                            loadCardRequestGroup(pg);
                            break;
                        case 5://Products Management
                            loadProductsManagementGroup(pg);
                            break;
                        case 6://Customer Management
                            loadCustomerManagementGroup(pg);
                            break;
                        case 7://Cards Management
                            loadCardsManagementGroup(pg);
                            break;
                        case 8://Accounts Management
                            loadAccountsManagementGroup(pg);
                            break;  
                        case 9://Rates Management
                            loadRatesManagementGroup(pg);
                            break; 
                        case 10://Configuration Menu
                            loadConfigurationMenuGroup(pg);
                            break; 
                        default:
                            break;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
    
    private void loadBasicTableManagementGroup(PermissionGroup permissionGroup) {
        Listgroup listgroup = createListGroup(permissionGroup);
        createCell(Constants.LIST_COUNTRY, "listCountry.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_CURRENCY, "listCurrency.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_CLASSIFICATION_PERSON, "listClassificationPerson.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_STATE, "listState.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_PHONE_TYPE, "listPhoneType.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_PERSON_TYPE, "listPersonType.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_DOCUMENTS_PERSON_TYPE, "listDocumentsPersonType.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_PROFESSIONS, "listProfession.zul", permissionGroup, listgroup);
        
    }
    
    private void loadSecurityManagementGroup(PermissionGroup permissionGroup) {
        Listgroup listgroup = createListGroup(permissionGroup);
        createCell(Constants.LIST_USER, "listUser.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_PASSWORD_CHANGE_REQUEST, "listPasswordChangeRequest.zul", permissionGroup, listgroup);    
        createCell(Constants.LIST_PROFILE, "listProfile.zul", permissionGroup, listgroup);    
        createCell(Constants.LIST_PROFILE_LANGUAGE, "listProfileData.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_USER_PROFILES, "listUserHasProfile.zul", permissionGroup, listgroup);
        
        
    }
    
    private void loadProgramsManagementGroup(PermissionGroup permissionGroup) {
        Listgroup listgroup = createListGroup(permissionGroup);
        createCell(Constants.LIST_ISSUER, "listIssuer.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_CARD_PROGRAM_MANAGER, "listCardProgramManager.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_PROGRAM_OWNER, "listProgramOwner.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_INTERBANK_NETWORK, "listNetwork.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_BIN_SPONSOR, "listBinSponsor.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_PROGRAM, "listProgram.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_LOYALTY_PROGRAM, "listLoyalty.zul", permissionGroup, listgroup);
    }
    
    private void loadCardRequestGroup(PermissionGroup permissionGroup) {
        Listgroup listgroup = createListGroup(permissionGroup);
        createCell(Constants.LIST_REQUEST_TYPE, "listRequestType.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_COLLECTIONS_REQUEST, "listCollectionRequest.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_COLLECTION_TYPE, "listCollectionTypes.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_STATUS_REQUEST, "listStatusRequest.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_CARD_REQUEST, "listRequest.zul", permissionGroup, listgroup);     
    }
    
    private void loadProductsManagementGroup(PermissionGroup permissionGroup) {
        Listgroup listgroup = createListGroup(permissionGroup);
        createCell(Constants.LIST_PRODUCTS_MANAGEMENT, "listProduct.zul", permissionGroup, listgroup);    
    }
    
    private void loadCustomerManagementGroup(PermissionGroup permissionGroup) {
        Listgroup listgroup = createListGroup(permissionGroup);
        createCell(Constants.LIST_STATUS_CUSTOMER, "listStatusCustomer.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_CUSTOMER_MANAGEMENT, "listCustomer.zul", permissionGroup, listgroup);            
    } 
    
    private void loadCardsManagementGroup(PermissionGroup permissionGroup) {
        Listgroup listgroup = createListGroup(permissionGroup);
        createCell(Constants.CARD_ASSIGMENT, "listCardAssigment.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_CARD_MANAGEMENT, "listCardManager.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_CARD_RENEWAL_REQUEST, "listCardRenewalRequest.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_NEW_CARD_ISSUE_REQUEST, "listCardRenewalByCanceled.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_CARD_STATUS, "listCardStatus.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_PLASTIC_MANUFACTURER, "listPlasticManufacturer.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_PLASTIC_REQUEST, "listPlasticRequest.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_DELIVERY_REQUEST, "listDelivery.zul", permissionGroup, listgroup);
        
    }
    
    private void loadAccountsManagementGroup(PermissionGroup permissionGroup) {
        Listgroup listgroup = createListGroup(permissionGroup);
        createCell(Constants.LIST_ACCOUNT_TYPE, "listAccountType.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_SUB_ACCOUNT_TYPE, "listSubAccountType.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_ACCOUNT_PROPERTIES, "listAccountProperties.zul", permissionGroup, listgroup);
        createCell(Constants.LIST_CARD_ACCOUNT, "listAccountCard.zul", permissionGroup, listgroup);
    }
    
    private void loadRatesManagementGroup(PermissionGroup permissionGroup) {
        Listgroup listgroup = createListGroup(permissionGroup);
        createCell(Constants.LIST_TRANSACTION, "listTransaction.zul", permissionGroup, listgroup);        
        createCell(Constants.LIST_GENERAL_RATE, "TabGeneralRates.zul", permissionGroup, listgroup);  
        createCell(Constants.LIST_RATE_BY_PROGRAM, "TabRatesByProgram.zul", permissionGroup, listgroup);  
        createCell(Constants.LIST_RATE_BY_PRODUCT, "TabRatesByProduct.zul", permissionGroup, listgroup);  
        createCell(Constants.LIST_RATE_BY_CARD, "TabRatesByCard.zul", permissionGroup, listgroup);  
    }
    
    private void loadConfigurationMenuGroup(PermissionGroup permissionGroup) {
        Listgroup listgroup = createListGroup(permissionGroup);
        createCell(Constants.LIST_SYSTEM_OPTIONS, "listPermission.zul", permissionGroup, listgroup);        
        createCell(Constants.LIST_SYSTEM_OPTIONS_LANGUAGE, "listPermissionData.zul", permissionGroup, listgroup);  
        createCell(Constants.LIST_MENU_OPTIONS, "listPermissionGroup.zul", permissionGroup, listgroup);  
        createCell(Constants.LIST_MENU_OPTIONS_LANGUAGE, "listPermissionGroupData.zul", permissionGroup, listgroup);    
    }
    
    private void createCell(Long permissionId, String view, PermissionGroup permissionGroup, Listgroup listgroup) {
        Permission permission = loadPermission(permissionId);
        if (permission != null) {
            Listitem item = new Listitem();
            Listcell listCell = new Listcell();
            listCell.setLabel(permission.getPermissionDataByLanguageId(languageId).getAlias());
            listCell.addEventListener("onClick", new RedirectListener(view, permissionId, permissionGroup));
            listCell.setId(permission.getId().toString());
            if (Sessions.getCurrent().getAttribute(WebConstants.VIEW) != null && (Sessions.getCurrent().getAttribute(WebConstants.VIEW).equals(view))) {
                if ((!WebConstants.HOME_ADMIN_ZUL.equals("/" + view))) {
                    listgroup.setOpen(true);
                    listCell.setStyle("background-color: #D8D8D8");
                    listCell.setLabel(">> " + listCell.getLabel());
                }
            }
            listCell.setParent(item);
            item.setParent(lbxPermissions);
        }
    }
    
    private Listgroup createListGroup(PermissionGroup permissionGroup) {
        Listgroup listgroup = new Listgroup();
        listgroup.setOpen(false);
        Listcell listcell = new Listcell();
        listcell.setLabel(permissionGroup.getPermissionGroupDataByLanguageId(languageId).getAlias());
        listcell.setParent(listgroup);
        listgroup.setParent(lbxPermissions);
        return listgroup;
    }
    
    private boolean existPermissionInGroup(List<Permission> ps, Long permissionGroupId) {
        for (Permission p : ps) {
            if (p.getPermissionGroupId().getId().equals(permissionGroupId)) {
                return true;
            }
        }
        return false;
    }
    
    class RedirectListener implements EventListener {

        private String view = null;
        private Long permissionId = null;
        private PermissionGroup permissionGroup;

        public RedirectListener() {
        }

        public RedirectListener(String view, Long permissionId, PermissionGroup permissionGroup) {
            this.view = view;
            this.permissionId = permissionId;
            this.permissionGroup = permissionGroup;
        }
        
        @Override
        public void onEvent(Event event) throws UiException, InterruptedException {
            Executions.sendRedirect(view);
            Sessions.getCurrent().setAttribute(WebConstants.VIEW, view);
            Sessions.getCurrent().setAttribute(WebConstants.PERMISSION_GROUP, permissionGroup.getId());
        }
    }
    
    

    private void loadAccountData() {
        try {
            currentuser = AccessControl.loadCurrentUser();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        User user = (User) session.getAttribute(Constants.USER_OBJ_SESSION);
        ltcFullName.setLabel(user.getFirstNames() + " " + user.getLastNames());
        //TODO:
        ltcProfile.setLabel("Administrador");
        ltcLogin.setLabel(user.getLogin());
/*
        //Opcion RequestType         
        Listitem item = new Listitem();
        Listcell listCell = new Listcell();
        listCell.setLabel(Labels.getLabel("cms.menu.requestType.list"));;
        listCell.addEventListener("onClick", new RedirectListener("listRequestType.zul"));
        listCell.setParent(item);
        item.setParent(lbxPermissions);

        // Opcion Country
        Listitem item2 = new Listitem();
        Listcell listCell2 = new Listcell();
        listCell2.setLabel(Labels.getLabel("sp.common.country"));
        listCell2.addEventListener("onClick", new RedirectListener("listCountry.zul"));
        listCell2.setParent(item2);
        item2.setParent(lbxPermissions);

        /// Opcion Currency
        Listitem item3 = new Listitem();
        Listcell listCell3 = new Listcell();
        listCell3.setLabel(Labels.getLabel("sp.common.currency"));
        listCell3.addEventListener("onClick", new RedirectListener("listCurrency.zul"));
        listCell3.setParent(item3);
        item3.setParent(lbxPermissions);

        // Opcion statusCard
         Listitem item4 = new Listitem();
         Listcell listCell4 = new Listcell();
         listCell4.setLabel(Labels.getLabel("sp.common.statusCard"));
         listCell4.addEventListener("onClick", new RedirectListener("listCardStatus.zul"));
         listCell4.setParent(item4);
         item4.setParent(lbxPermissions);

         /// Opction status
         Listitem item5 = new Listitem();
         Listcell listCell5 = new Listcell();
         listCell5.setLabel(Labels.getLabel("cms.common.statusRequest"));
         listCell5.addEventListener("onClick", new RedirectListener("listStatusRequest.zul"));
         listCell5.setParent(item5);
         item5.setParent(lbxPermissions);
        
        // Opction collectionRequest
        Listitem item6 = new Listitem();
        Listcell listCell6 = new Listcell();
        listCell6.setLabel(Labels.getLabel("cms.common.collectionRequest"));
        listCell6.addEventListener("onClick", new RedirectListener("listCollectionRequest.zul"));
        listCell6.setParent(item6);
        item6.setParent(lbxPermissions);

        //Option Classification_Person
         Listitem item7 = new Listitem();
         Listcell listCell7 = new Listcell();
         listCell7.setLabel(Labels.getLabel("sp.commom.Classification.Person"));
         listCell7.addEventListener("onClick", new RedirectListener("listClassificationPerson.zul"));
         listCell7.setParent(item7);
         item7.setParent(lbxPermissions);

         // Option State
         Listitem item8 = new Listitem();
         Listcell listCell8 = new Listcell();
         listCell8.setLabel(Labels.getLabel("cms.common.name.state"));
         listCell8.addEventListener("onClick", new RedirectListener("listState.zul"));
         listCell8.setParent(item8);
         item8.setParent(lbxPermissions);
        
        //Option CollectionTypes
        Listitem item9 = new Listitem();
        Listcell listCell9 = new Listcell();
        listCell9.setLabel(Labels.getLabel("cms.requestCollection.collectionTypes"));
        listCell9.addEventListener("onClick", new RedirectListener("listCollectionTypes.zul"));
        listCell9.setParent(item9);
        item9.setParent(lbxPermissions);

        // Option Program
        Listitem item10 = new Listitem();
        Listcell listCell10 = new Listcell();
        listCell10.setLabel(Labels.getLabel("cms.common.Program"));
        listCell10.addEventListener("onClick", new RedirectListener("listProgram.zul"));
        listCell10.setParent(item10);
        item10.setParent(lbxPermissions);

        // Option Request
        Listitem item11 = new Listitem();
        Listcell listCell11 = new Listcell();
        listCell11.setLabel(Labels.getLabel("cms.menu.request.list"));
        listCell11.addEventListener("onClick", new RedirectListener("listRequest.zul"));
        listCell11.setParent(item11);
        item11.setParent(lbxPermissions);

        // Option Transaction
        Listitem item12 = new Listitem();
        Listcell listCell12 = new Listcell();
        listCell12.setLabel(Labels.getLabel("cms.menu.transactions"));
        listCell12.addEventListener("onClick", new RedirectListener("listTransaction.zul"));
        listCell12.setParent(item12);
        item12.setParent(lbxPermissions);

        // Option Issuer
        Listitem item13 = new Listitem();
        Listcell listCell13 = new Listcell();
        listCell13.setLabel(Labels.getLabel("cms.menu.card.issuer"));
        listCell13.addEventListener("onClick", new RedirectListener("listIssuer.zul"));
        listCell13.setParent(item13);
        item13.setParent(lbxPermissions);

        // Option Loyalty
        Listitem item14 = new Listitem();
        Listcell listCell14 = new Listcell();
        listCell14.setLabel(Labels.getLabel("cms.menu.loyalty"));
        listCell14.addEventListener("onClick", new RedirectListener("listLoyalty.zul"));
        listCell14.setParent(item14);
        item14.setParent(lbxPermissions);

        // Option PhoneType
         Listitem item15 = new Listitem();
         Listcell listCell15 = new Listcell();
         listCell15.setLabel(Labels.getLabel("cms.menu.phoneType.list"));
         listCell15.addEventListener("onClick", new RedirectListener("listPhoneType.zul"));
         listCell15.setParent(item15);
         item15.setParent(lbxPermissions);

         // Option PersonType
         Listitem item16 = new Listitem();
         Listcell listCell16 = new Listcell();
         listCell16.setLabel(Labels.getLabel("cms.menu.personType.list"));
         listCell16.addEventListener("onClick", new RedirectListener("listPersonType.zul"));
         listCell16.setParent(item16);
         item16.setParent(lbxPermissions);

         // Option DocumentsPersonType
         Listitem item17 = new Listitem();
         Listcell listCell17 = new Listcell();
         listCell17.setLabel(Labels.getLabel("cms.menu.documentspersonType.list"));
         listCell17.addEventListener("onClick", new RedirectListener("listDocumentsPersonType.zul"));
         listCell17.setParent(item17);
         item17.setParent(lbxPermissions);
        
        // Option Product
        Listitem item18 = new Listitem();
        Listcell listCell18 = new Listcell();
        listCell18.setLabel(Labels.getLabel("cms.menu.product"));
        listCell18.addEventListener("onClick", new RedirectListener("listProduct.zul"));
        listCell18.setParent(item18);
        item18.setParent(lbxPermissions);

        //Opción General Rates
        Listitem item19 = new Listitem();
        Listcell listCell19 = new Listcell();
        listCell19.setLabel(Labels.getLabel("cms.menu.generalRate.list"));
        listCell19.addEventListener("onClick", new RedirectListener("TabGeneralRates.zul"));
        listCell19.setParent(item19);
        item19.setParent(lbxPermissions);

        //Opción Account Manager
        Listitem item20 = new Listitem();
        Listcell listCell20 = new Listcell();
        listCell20.setLabel(Labels.getLabel("cms.menu.accountManager.list"));
        listCell20.addEventListener("onClick", new RedirectListener("listAccountProperties.zul"));
        listCell20.setParent(item20);
        item20.setParent(lbxPermissions);

        //Opción List Customer
        Listitem item21 = new Listitem();
        Listcell listCell21 = new Listcell();
        listCell21.setLabel(Labels.getLabel("cms.menu.customer.list"));
        listCell21.addEventListener("onClick", new RedirectListener("listCustomer.zul"));
        listCell21.setParent(item21);
        item21.setParent(lbxPermissions);
        
        //Opción PlasticManufacturer
        Listitem item22 = new Listitem();
        Listcell listCell22 = new Listcell();
        listCell22.setLabel(Labels.getLabel("cms.menu.plasticManufacturer"));
        listCell22.addEventListener("onClick", new RedirectListener("listPlasticManufacturer.zul"));
        listCell22.loadOperationalManagementGroupsetParent(item22);
        item22.setParent(lbxPermissions);

        //Opción Account Type
        Listitem item23 = new Listitem();
        Listcell listCell23 = new Listcell();
        listCell23.setLabel(Labels.getLabel("cms.menu.accountType"));
        listCell23.addEventListener("onClick", new RedirectListener("listAccountType.zul"));
        listCell23.setParent(item23);
        item23.setParent(lbxPermissions);

        //Opción Sub Account Type
        Listitem item24 = new Listitem();
        Listcell listCell24 = new Listcell();
        listCell24.setLabel(Labels.getLabel("cms.menu.subAccountType"));
        listCell24.addEventListener("onClick", new RedirectListener("listSubAccountType.zul"));
        listCell24.setParent(item24);
        item24.setParent(lbxPermissions);
        
         //Opción User
        Listitem item25 = new Listitem();
        Listcell listCell25 = new Listcell();
        listCell25.setLabel(Labels.getLabel("cms.menu.user"));
        listCell25.addEventListener("onClick", new RedirectListener("listUser.zul"));
        listCell25.setParent(item25);
        item25.setParent(lbxPermissions);
        
        //Opción Card Assignment
        Listitem item26 = new Listitem();
        Listcell listCell26 = new Listcell();
        listCell26.setLabel(Labels.getLabel("cms.menu.cardAssigment"));
        listCell26.addEventListener("onClick", new RedirectListener("listCardAssigment.zul"));
        listCell26.setParent(item26);
        item26.setParent(lbxPermissions);
        
        //Opción Program Owner
        Listitem item27 = new Listitem();
        Listcell listCell27 = new Listcell();
        listCell27.setLabel(Labels.getLabel("cms.menu.programOwner"));
        listCell27.addEventListener("onClick", new RedirectListener("listProgramOwner.zul"));
        listCell27.setParent(item27);
        item27.setParent(lbxPermissions);
        
        //Opción plastic request
        Listitem item28 = new Listitem();
        Listcell listCell28 = new Listcell();
        listCell28.setLabel(Labels.getLabel("cms.common.menu.plasticRequest"));
        listCell28.addEventListener("onClick", new RedirectListener("listPlasticRequest.zul"));
        listCell28.setParent(item28);
        item28.setParent(lbxPermissions);
        
        //Opción Card Manager
        Listitem item29 = new Listitem();
        Listcell listCell29 = new Listcell();
        listCell29.setLabel(Labels.getLabel("cms.menu.cardManager"));
        listCell29.addEventListener("onClick", new RedirectListener("listCardManager.zul"));
        listCell29.setParent(item29);
        item29.setParent(lbxPermissions);

        //Opción Rates By Program
        Listitem item30 = new Listitem();
        Listcell listCell30 = new Listcell();
        listCell30.setLabel(Labels.getLabel("cms.menu.rateByProgram.list"));
        listCell30.addEventListener("onClick", new RedirectListener("TabRatesByProgram.zul"));
        listCell30.setParent(item30);
        item30.setParent(lbxPermissions);

        //Opción Rates By Product
        Listitem item31 = new Listitem();
        Listcell listCell31 = new Listcell();
        listCell31.setLabel(Labels.getLabel("cms.menu.rate.product"));
        listCell31.addEventListener("onClick", new RedirectListener("TabRatesByProduct.zul"));
        listCell31.setParent(item31);
        item31.setParent(lbxPermissions);
        
         //Opción RateByCardHolder
        Listitem item32 = new Listitem();
        Listcell listCell32 = new Listcell();
        listCell32.setLabel(Labels.getLabel("cms.menu.rateByCard.list"));
        listCell32.addEventListener("onClick", new RedirectListener("TabRatesByCard.zul"));
        listCell32.setParent(item32);
        item32.setParent(lbxPermissions);
        
        //Opción Delivery 
        Listitem item33 = new Listitem();
        Listcell listCell33 = new Listcell();
        listCell33.setLabel(Labels.getLabel("cms.menu.manageDeliveryCards"));
        listCell33.addEventListener("onClick", new RedirectListener("listDelivery.zul"));
        listCell33.setParent(item33);
        item33.setParent(lbxPermissions);
         
        //Opción CardRenewalRequest 
        Listitem item34 = new Listitem();
        Listcell listCell34 = new Listcell();
        listCell34.setLabel(Labels.getLabel("cms.menu.cardRenewal"));
        listCell34.addEventListener("onClick", new RedirectListener("listCardRenewalRequest.zul"));
        listCell34.setParent(item34);
        item34.setParent(lbxPermissions);
        
//        //Opción CardRenewalRequest
//        Listitem item91 = new Listitem();
//        Listcell listCell91 = new Listcell();
//        listCell91.setLabel(Labels.getLabel("cms.menu.cardRenewal"));
//        listCell91.addEventListener("onClick", new RedirectListener("listCardRenewalRequest.zul"));
//        listCell91.setParent(item91);
//        item91.setParent(lbxPermissions);
        
//        //Opción Card Activation 
//        Listitem item35 = new Listitem();
//        Listcell listCell35 = new Listcell();
//        listCell35.setLabel(Labels.getLabel("cms.menu.cardActivation"));
//        listCell35.addEventListener("onClick", new RedirectListener("listCardActivation.zul"));
//        listCell35.setParent(item35);
//        item35.setParent(lbxPermissions);
        
         //Opción RenewalByCanceled 
        Listitem item36 = new Listitem();
        Listcell listCell36 = new Listcell();
        listCell36.setLabel(Labels.getLabel("cms.menu.cardRenewalByCancelled"));
        listCell36.addEventListener("onClick", new RedirectListener("listCardRenewalByCanceled.zul"));
        listCell36.setParent(item36);
        item36.setParent(lbxPermissions);
        
        //Opción Profesion 
        Listitem item41 = new Listitem();
        Listcell listCell41 = new Listcell();
        listCell41.setLabel(Labels.getLabel("cms.menu.profession"));
        listCell41.addEventListener("onClick", new RedirectListener("listProfession.zul"));
        listCell41.setParent(item41);
        item41.setParent(lbxPermissions);    

        //Opción Card Program Manager 
        Listitem item42 = new Listitem();
        Listcell listCell42 = new Listcell();
        listCell42.setLabel(Labels.getLabel("cms.menu.card.program.manager"));
        listCell42.addEventListener("onClick", new RedirectListener("listCardProgramManager.zul"));
        listCell42.setParent(item42);
        item42.setParent(lbxPermissions);
        
        //Opción Permission 
        Listitem item44 = new Listitem();
        Listcell listCell44 = new Listcell();
        listCell44.setLabel(Labels.getLabel("cms.menu.permission"));
        listCell44.addEventListener("onClick", new RedirectListener("listPermission.zul"));
        listCell44.setParent(item44);
        item44.setParent(lbxPermissions);
        
        //Opción Permission Data
        Listitem item45 = new Listitem();
        Listcell listCell45 = new Listcell();
        listCell45.setLabel(Labels.getLabel("cms.menu.permission.data"));
        listCell45.addEventListener("onClick", new RedirectListener("listPermissionData.zul"));
        listCell45.setParent(item45);
        item45.setParent(lbxPermissions);
        
        //Opción Permission Group
        Listitem item46 = new Listitem();
        Listcell listCell46 = new Listcell();
        listCell46.setLabel(Labels.getLabel("cms.menu.permission.group"));
        listCell46.addEventListener("onClick", new RedirectListener("listPermissionGroup.zul"));
        listCell46.setParent(item46);
        item46.setParent(lbxPermissions);
        
        //Opción Permission Group Data
        Listitem item47 = new Listitem();
        Listcell listCell47 = new Listcell();
        listCell47.setLabel(Labels.getLabel("cms.menu.permission.group.data"));
        listCell47.addEventListener("onClick", new RedirectListener("listPermissionGroupData.zul"));
        listCell47.setParent(item47);
        item47.setParent(lbxPermissions);
        
        //Opción Profile 
        Listitem item48 = new Listitem();
        Listcell listCell48 = new Listcell();
        listCell48.setLabel(Labels.getLabel("cms.menu.profile"));
        listCell48.addEventListener("onClick", new RedirectListener("listProfile.zul"));
        listCell48.setParent(item48);
        item48.setParent(lbxPermissions); 
        
        //Opción Profile Data 
        Listitem item49 = new Listitem();
        Listcell listCell49 = new Listcell();
        listCell49.setLabel(Labels.getLabel("cms.menu.profile.data"));
        listCell49.addEventListener("onClick", new RedirectListener("listProfileData.zul"));
        listCell49.setParent(item49);
        item49.setParent(lbxPermissions);
        
        //Opción Network 
        Listitem item50 = new Listitem();
        Listcell listCell50 = new Listcell();
        listCell50.setLabel(Labels.getLabel("cms.menu.network"));
        listCell50.addEventListener("onClick", new RedirectListener("listNetwork.zul"));
        listCell50.setParent(item50);
        item50.setParent(lbxPermissions);
        
         //Opción ACCOUNT CARD 
        Listitem item51 = new Listitem();
        Listcell listCell51 = new Listcell();
        listCell51.setLabel(Labels.getLabel("cms.menu.account.card"));
        listCell51.addEventListener("onClick", new RedirectListener("listAccountCard.zul"));
        listCell51.setParent(item51);
        item51.setParent(lbxPermissions);
        
        //Opción PASSWORD CHANGE REQUEST 
        Listitem item52 = new Listitem();
        Listcell listCell52 = new Listcell();
        listCell52.setLabel(Labels.getLabel("cms.menu.password.change.request"));
        listCell52.addEventListener("onClick", new RedirectListener("listPasswordChangeRequest.zul"));
        listCell52.setParent(item52);
        item52.setParent(lbxPermissions);
        
        // Opcion STATUS CUSTOMER
        Listitem item53 = new Listitem();
        Listcell listCell53 = new Listcell();
        listCell53.setLabel(Labels.getLabel("cms.menu.statusCustomer"));
        listCell53.addEventListener("onClick", new RedirectListener("listStatusCustomer.zul"));
        listCell53.setParent(item53);
        item53.setParent(lbxPermissions);
        
        //Opción BINSPONSOR 
        Listitem item55 = new Listitem();
        Listcell listCell55 = new Listcell();
        listCell55.setLabel(Labels.getLabel("cms.menu.bin.sponsor"));
        listCell55.addEventListener("onClick", new RedirectListener("listBinSponsor.zul"));
        listCell55.setParent(item55);
        item55.setParent(lbxPermissions);
        
              
        //Opción UserHasProfile 
        Listitem item90 = new Listitem();
        Listcell listCell90 = new Listcell();
        listCell90.setLabel(Labels.getLabel("cms.crud.userHasProfile.list"));
        listCell90.addEventListener("onClick", new RedirectListener("listUserHasProfile.zul"));
        listCell90.setParent(item90);
        item90.setParent(lbxPermissions);
   */     
        
    }

        public void onEvent(Event event) throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

class RedirectListener implements EventListener {

    private String view = null;
    private Long permissionId = null;

    public RedirectListener() {
    }

    public RedirectListener(String view, Long permissionId) {
        this.view = view;
        this.permissionId = permissionId;
    }

    public RedirectListener(String view) {
        this.view = view;
    }

    @Override
    public void onEvent(Event event) throws UiException, InterruptedException {
        Executions.sendRedirect(view);
    }
}
