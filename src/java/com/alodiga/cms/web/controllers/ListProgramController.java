package com.alodiga.cms.web.controllers;
import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.custom.components.ListcellEditButton;
import com.alodiga.cms.web.custom.components.ListcellViewButton;
import com.alodiga.cms.web.generic.controllers.GenericAbstractListController;
import static com.alodiga.cms.web.generic.controllers.GenericDistributionController.request;
import com.alodiga.cms.web.utils.Utils;
import com.alodiga.cms.web.utils.Utils;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.Program;


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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

public class ListProgramController extends GenericAbstractListController<Program> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtName;
    //private Datebox ;
    private List<Program> programs = null;
    private ProgramEJB programEJB = null;
    private UtilsEJB utilsEJB = null;
   
    //private User currentUser;
    //private Profile currentProfile;
    
    
    //private User currentUser;
    //private Profile currentProfile;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
        
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            //Evaluar Permisos
            permissionEdit = true;
            permissionAdd = true; 
            permissionRead = true;
            //
            adminPage = "adminProgram.zul";
            programEJB = (ProgramEJB) EJBServiceLocator.getInstance().get(EjbConstants.PROGRAM_EJB);
            getData();
            //List<Program> program = null ;
            loadList(programs);
        } catch (Exception ex) {
            showError(ex);
        }
    }
 //   public void ProgramListener() {
   // }


    

//    public List<Country> getFilteredList(String filter) {
//        List<Country> countriesaux = new ArrayList<Country>();
//        Country country;
//        try {
//            if (filter != null && !filter.equals("")) {
//                country = utilsEJB.searchCountry(filter);
//                countriesaux.add(country);
//            } else {
//                return countries;
//            }
//        } catch (RegisterNotFoundException ex) {
//            Logger.getLogger(ListCountryController.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (Exception ex) {
//            showError(ex);
//        }
//        return countriesaux;
//    }

    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_ADD);
        Executions.getCurrent().sendRedirect(adminPage);
    }

    public void onClick$btnDelete() {
        
        
        
        
    }

    public void loadList(List<Program> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                //btnDownload.setVisible(true);
                for (Program program : list) {
                    item = new Listitem();
                    item.setValue(program);
                    item.appendChild(new Listcell(program.getName()));
                    item.appendChild(new Listcell(program.getDescription()));
                    item.appendChild(new Listcell(program.getContractDate().toString()));
                    item.appendChild(new Listcell(program.getProgramTypeId().getName()));
                    item.appendChild(new Listcell(program.getProductTypeId().getName()));
                    item.appendChild(new Listcell(program.getIssuerId().getName()));                
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, program) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, program) : new Listcell());
                    item.setParent(lbxRecords);
                }
            } else {
                btnDownload.setVisible(false);
                item = new Listitem();
                item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
               /* item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());*/
                item.setParent(lbxRecords);
            }

        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void getData() {
        try {
            programs = new ArrayList<Program>();
            request.setFirst(0);
            request.setLimit(null);
            programs = programEJB.getProgram(request);//getProgram(request);
        } catch (EmptyListException ex) {
            Logger.getLogger(ListProgramController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralException ex) {
            Logger.getLogger(ListProgramController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullParameterException ex) {
            Logger.getLogger(ListProgramController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private void showEmptyList(){
                Listitem item = new Listitem();
                item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                /*item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());*/
                item.setParent(lbxRecords);  
    }

    public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("sp.crud.country.list"));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {
        txtName.setText("");
    }

//    public void onClick$btnSearch() throws InterruptedException {
//        try {
//            loadList(getFilteredList(txtAlias.getText()));
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }

    @Override
    public List<Program> getFilterList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void loadDataList(List<Program> list) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

  

    public void startListener() {
        
    }
    

}