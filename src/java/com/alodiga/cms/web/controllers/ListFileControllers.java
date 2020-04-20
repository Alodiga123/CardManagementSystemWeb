package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.CardEJB;
import com.alodiga.cms.commons.ejb.RequestEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractListController;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.PlasticCustomizingRequest;
import com.cms.commons.models.Product;
import com.cms.commons.models.ResultPlasticCustomizingRequest;
import com.cms.commons.models.StatusResultPlasticCustomizing;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

public class ListFileControllers extends GenericAbstractListController<ResultPlasticCustomizingRequest> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Label lblNameFile;
    private RequestEJB requestEJB = null;
    private PlasticCustomizingRequest plastiCustomerParam;
    private static List<String[]> readList = null;
    private Button btnRead;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            requestEJB = (RequestEJB) EJBServiceLocator.getInstance().get(EjbConstants.REQUEST_EJB);
            loadField();

        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnRead() throws InterruptedException {
        leer();
        loadFile(readList);
    }

    private void loadField() {
        String nombreArchivo = "archivo_prueba.csv";
        
        AdminPlasticRequestController adminPlasticRequest = new AdminPlasticRequestController();
        if (adminPlasticRequest.getPlasticCustomizingRequest().getId() != null) {
            plastiCustomerParam = adminPlasticRequest.getPlasticCustomizingRequest();
        }
        
        lblNameFile.setValue(nombreArchivo);
    }

    public static void leer() {
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;

        try {
            // Apertura del fichero y creacion de BufferedReader para poder hacer una lectura comoda
            archivo = new File("/home/usuario/Documentos/archivo_prueba.csv");
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);
            readList = new ArrayList<String[]>();

            // Lectura del fichero  
            System.out.println("Leyendo el contendio del archivo.txt");
            String linea;
            while ((linea = br.readLine()) != null) {

                System.out.println(linea);
                String[] datos = linea.split(";");
                readList.add(datos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // cerramos el fichero, para asegurarnos que se cierra tanto si todo va bien como si falla
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public void loadFile(List<String[]> archivo) {
        int statusResultFile = 0;
        String statusDes = null;
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (archivo != null && !archivo.isEmpty()) {
                for (int i = 0; i < archivo.size(); i++) {
                    String[] linea = archivo.get(i);
                    item = new Listitem();
                    item.setValue(linea);

                    EJBRequest request1 = new EJBRequest();
                    Map params = new HashMap();
                    params.put(Constants.PLASTIC_MANUFACTURER_KEY,plastiCustomerParam.getPlasticManufacturerId().getId());
                    request1.setParams(params);
                    List<StatusResultPlasticCustomizing> statusResultPlasticCustomizingList = requestEJB.getStatusByPlasticManufacturer(request1);
                    for (StatusResultPlasticCustomizing statusResult : statusResultPlasticCustomizingList) {
                        statusResultFile = Integer.parseInt(linea[5].trim());
                        if (statusResultFile == statusResult.getStatusPlasticCustomizingRequestd().getId()) {
                                statusDes = statusResult.getStatusPlasticCustomizingRequestd().getDescription();
                        }                              
                    }
                    
                    item.appendChild(new Listcell(linea[0]));
                    item.appendChild(new Listcell(linea[3]));
                    item.appendChild(new Listcell(linea[2]));
                    item.appendChild(new Listcell(statusDes));
                    item.setParent(lbxRecords);
                }
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnLoadData() throws InterruptedException {
        try {
            saveResult(readList);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void saveResult(List<String[]> archivo) throws ParseException, EmptyListException {
        ResultPlasticCustomizingRequest resultPlasticCustomizingRequest = null;
        StatusResultPlasticCustomizing statusResultParam = null;
        int statusResultFile = 0;
        try {
            if (archivo != null && !archivo.isEmpty()) {
                for (int i = 0; i < archivo.size(); i++) {

                    String[] linea = archivo.get(i);
                    String pattern = "yyyy-MM-dd";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    resultPlasticCustomizingRequest = new ResultPlasticCustomizingRequest();

                    resultPlasticCustomizingRequest.setCardNumber(linea[0]);
                    resultPlasticCustomizingRequest.setCardHolder(linea[2]);
                    resultPlasticCustomizingRequest.setIdentificationNumberCardHolder(linea[1]);
                    resultPlasticCustomizingRequest.setProductTypeDescription(linea[4]);
                    resultPlasticCustomizingRequest.setExpirationCardDate(simpleDateFormat.parse(linea[3]));
                    resultPlasticCustomizingRequest.setStatusResult(linea[5]);
                    resultPlasticCustomizingRequest.setPlasticCustomizingRequestId(plastiCustomerParam);                    
                    
                    //Actualiza el estatus de la personalización de tarjetas
                    EJBRequest request1 = new EJBRequest();
                    Map params = new HashMap();
                    params.put(Constants.PLASTIC_MANUFACTURER_KEY,plastiCustomerParam.getPlasticManufacturerId().getId());
                    request1.setParams(params);
                    List<StatusResultPlasticCustomizing> statusResultPlasticCustomizingList = requestEJB.getStatusByPlasticManufacturer(request1);
                    for (StatusResultPlasticCustomizing statusResult : statusResultPlasticCustomizingList) {
                        statusResultFile = Integer.parseInt(linea[5].trim());
                        if (statusResultFile == statusResult.getStatusPlasticCustomizingRequestd().getId()) {
                                resultPlasticCustomizingRequest.setStatusResultPlasticCustomizingId(statusResult);
                        }                              
                    }
                    
                    //Guarda la lÍnea del archivo
                    resultPlasticCustomizingRequest = requestEJB.saveResultPlasticCustomizingRequest(resultPlasticCustomizingRequest);
                }
                this.showMessage("cms.common.msj.assignPlasticCard", false, null);
                btnRead.setVisible(false);

            }
        } catch (GeneralException ex) {
            Logger.getLogger(ListCardAssigmentControllers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullParameterException ex) {
            Logger.getLogger(ListCardAssigmentControllers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<ResultPlasticCustomizingRequest> getFilterList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void startListener() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void getData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void loadDataList(List<ResultPlasticCustomizingRequest> list) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
