package Controller;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportController {
    public void cetakLaporanDariList(List<?> dataList) {
        try {
        String reportPath = "src/umkm/laporanhistory.jasper"; 
        
        net.sf.jasperreports.engine.data.JRBeanCollectionDataSource dataSource = 
            new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(dataList);
        
        java.util.HashMap<String, Object> parameters = new java.util.HashMap<>();
        
        net.sf.jasperreports.engine.JasperPrint jasperPrint = 
            net.sf.jasperreports.engine.JasperFillManager.fillReport(reportPath, parameters, dataSource);
        
        net.sf.jasperreports.view.JasperViewer.viewReport(jasperPrint, false);
        
    } catch (Exception e) {
        javax.swing.JOptionPane.showMessageDialog(null, "Gagal mencetak dari List: " + e.getMessage());
        e.printStackTrace();
    }
    }
}