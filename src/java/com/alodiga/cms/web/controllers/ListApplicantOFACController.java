package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.RequestEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractListController;
import com.alodiga.cms.web.utils.Utils;
import com.alodiga.cms.web.utils.WebConstants;
//import com.alodiga.ws.remittance.services.WSOFACMethodProxy;
//import com.alodiga.ws.remittance.services.WsExcludeListResponse;
//import com.alodiga.ws.remittance.services.WsLoginResponse;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.ApplicantNaturalPerson;
import com.cms.commons.models.Request;
import com.cms.commons.models.User;
import com.cms.commons.models.StatusApplicant;
import com.cms.commons.models.StatusRequest;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ListApplicantOFACController extends GenericAbstractListController<ApplicantNaturalPerson> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtName;
    private PersonEJB personEJB = null;
    private RequestEJB requestEJB = null;
    private UtilsEJB utilsEJB = null;
    private List<ApplicantNaturalPerson> applicantList = null;
    private User currentUser;
    private Button btnSave;
    private AdminRequestController adminRequest = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
        startListener();
    }

    public void startListener() {
        EventQueue que = EventQueues.lookup("updateApplicantOFAC", EventQueues.APPLICATION, true);
        que.subscribe(new EventListener() {
            public void onEvent(Event evt) {
                getData();
                loadDataList(applicantList);
            }
        });
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            //Evaluar Permisos
            permissionEdit = true;
            permissionAdd = true;
            permissionRead = true;
            adminPage = "/adminApplicantOFAC.zul";
            adminRequest = new AdminRequestController();
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            requestEJB = (RequestEJB) EJBServiceLocator.getInstance().get(EjbConstants.REQUEST_EJB);
            getData();
            loadDataList(applicantList);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void getData() {
        applicantList = new ArrayList<ApplicantNaturalPerson>();
        ApplicantNaturalPerson applicantNaturalPerson = null;
        try {
            //Solicitante de Tarjeta
            AdminNaturalPersonController adminNaturalPerson = new AdminNaturalPersonController();
            if (adminNaturalPerson.getApplicantNaturalPerson() != null) {
                applicantNaturalPerson = adminNaturalPerson.getApplicantNaturalPerson();
            }
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.APPLICANT_NATURAL_PERSON_KEY, applicantNaturalPerson.getId());
            request1.setParams(params);
            applicantList = personEJB.getCardComplementaryByApplicant(request1);   
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } finally {
            applicantList.add(applicantNaturalPerson);
        }
    }  

    public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("cms.crud.additionalCards.list"));
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
//    public void onClick$btnReviewOFAC() {
//        int indBlackList = 0;
//        String lastName = "";
//        String firstName = "";
//        float ofacPercentege = 0.5F;
//        Request request = adminRequest.getRequest();
//        WSOFACMethodProxy ofac = new WSOFACMethodProxy();
//        try {
//            WsLoginResponse loginResponse = new WsLoginResponse();
//            loginResponse = ofac.loginWS("alodiga", "d6f80e647631bb4522392aff53370502");
//            WsExcludeListResponse ofacResponse = new WsExcludeListResponse();   
//            for (ApplicantNaturalPerson applicant: applicantList) {
//                lastName = applicant.getLastNames();
//                firstName = applicant.getFirstNames();
//                ofacResponse = ofac.queryOFACList(loginResponse.getToken(),lastName, firstName, null, null, null, null, ofacPercentege);
//                
//                //Guardar el resultado de revisión en lista OFAC para cada solicitante
//                ReviewOFAC reviewOFAC = new ReviewOFAC();
//                reviewOFAC.setPersonId(applicant.getPersonId());
//                reviewOFAC.setRequestId(request);
//                reviewOFAC.setResultReview(ofacResponse.getPercentMatch());
//                reviewOFAC = requestEJB.saveReviewOFAC(reviewOFAC);
//                
//                //Actualizar el estatus del solicitante si tiene coincidencia con lista OFAC
//                if (Double.parseDouble(ofacResponse.getPercentMatch()) >= 0.80) {
//                    applicant.setStatusApplicantId(getStatusApplicant(applicant, Constants.STATUS_APPLICANT_BLACK_LIST));
//                    indBlackList = 1;
//                } else {
//                  applicant.setStatusApplicantId(getStatusApplicant(applicant, Constants.STATUS_APPLICANT_BLACK_LIST_OK));  
//                }
//                applicant = personEJB.saveApplicantNaturalPerson(applicant);
//            }
//            //Si algun(os) solicitante(s) coincide(n) con la Lista OFAC se actualiza estatus de la solicitud
//            if (indBlackList == 1) {
//                request.setStatusRequestId(getStatusRequest(request,Constants.STATUS_REQUEST_PENDING_APPROVAL));
//            } else {
//                request.setStatusRequestId(getStatusRequest(request,Constants.STATUS_REQUEST_BLACK_LIST_OK));
//            }
//            request = requestEJB.saveRequest(request);
//            getData();
//            loadDataList(applicantList);
//            this.showMessage("sp.common.finishReviewOFAC", false, null);
//	} catch (RemoteException e) {
//            e.printStackTrace();
//	} catch (Exception ex) {
//            showError(ex);
//        }
//    }
    
    public StatusApplicant getStatusApplicant(ApplicantNaturalPerson applicant, int statusApplicantId) {
        StatusApplicant statusApplicant = applicant.getStatusApplicantId();
        try {
            EJBRequest request = new EJBRequest();
            request.setParam(statusApplicantId);
            statusApplicant = requestEJB.loadStatusApplicant(request);
        } catch (Exception ex) {
            showError(ex);
        }
        return statusApplicant;
    }
    
    public StatusRequest getStatusRequest(Request requestCard, int statusRequestId) {
        StatusRequest statusRequest = requestCard.getStatusRequestId();
        try {
            EJBRequest request = new EJBRequest();
            request.setParam(statusRequestId);
            statusRequest = requestEJB.loadStatusRequest(request);
        } catch (Exception ex) {
            showError(ex);
        }
        return statusRequest;
    }

    public void onClick$btnClear() throws InterruptedException {
        txtName.setText("");
    }    
    
    public void loadDataList(List<ApplicantNaturalPerson> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                for (ApplicantNaturalPerson applicantNaturalPerson : list) {
                    item = new Listitem();
                    item.setValue(applicantNaturalPerson);
                    StringBuilder builder = new StringBuilder(applicantNaturalPerson.getFirstNames());
                    builder.append(" ");
                    builder.append(applicantNaturalPerson.getLastNames());                    
                    item.appendChild(new Listcell(builder.toString()));
                    item.appendChild(new Listcell(applicantNaturalPerson.getDocumentsPersonTypeId().getDescription()));
                    item.appendChild(new Listcell(applicantNaturalPerson.getIdentificationNumber()));
                    if (applicantNaturalPerson.getKinShipApplicantId() == null) {
                        item.appendChild(new Listcell(WebConstants.MAIN_APPLICANT));
                    } else {
                        item.appendChild(new Listcell(applicantNaturalPerson.getKinShipApplicantId().getDescription()));
                    }
                    item.appendChild(new Listcell(applicantNaturalPerson.getStatusApplicantId().getDescription()));
                    item.appendChild(createButtonEditModal(applicantNaturalPerson));
                    item.appendChild(createButtonViewModal(applicantNaturalPerson));
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
    
    public Listcell createButtonEditModal(final Object obg) {
       Listcell listcellEditModal = new Listcell();
        try {    
            Button button = new Button();
            button.setImage("/images/icon-edit.png");
            button.setTooltiptext(Labels.getLabel("sp.common.actions.edit"));
            button.setClass("open orange");
            button.addEventListener("onClick", new EventListener() {
                @Override
                public void onEvent(Event arg0) throws Exception {
                  Sessions.getCurrent().setAttribute("object", obg);  
                  Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_EDIT);
                  Map<String, Object> paramsPass = new HashMap<String, Object>();
                  paramsPass.put("object", obg);
                  final Window window = (Window) Executions.createComponents(adminPage, null, paramsPass);
                  window.doModal(); 
                }
            });
            button.setParent(listcellEditModal);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listcellEditModal;
    }
    
    public Listcell createButtonViewModal(final Object obg) {
       Listcell listcellViewModal = new Listcell();
        try {    
            Button button = new Button();
            button.setImage("/images/icon-invoice.png");
            button.setTooltiptext(Labels.getLabel("sp.common.actions.view"));
            button.setClass("open orange");
            button.addEventListener("onClick", new EventListener() {
                @Override
                public void onEvent(Event arg0) throws Exception {
                  Sessions.getCurrent().setAttribute("object", obg);  
                  Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_VIEW);
                  Map<String, Object> paramsPass = new HashMap<String, Object>();
                  paramsPass.put("object", obg);
                  final Window window = (Window) Executions.createComponents(adminPage, null, paramsPass);
                  window.doModal(); 
                }

            });
            button.setParent(listcellViewModal);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listcellViewModal;
    }

    @Override
    public List<ApplicantNaturalPerson> getFilterList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}