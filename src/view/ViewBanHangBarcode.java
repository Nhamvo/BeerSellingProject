/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;

import com.google.zxing.common.HybridBinarizer;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import service.GioHangBarcodeService;
import service_impl.GioHangBarcodeImpl;
import viewmodels.GioHangBarCodeViewModel;
import viewmodels.GioHangViewModel;

/**
 *
 * @author Nham
 */
public class ViewBanHangBarcode extends JFrame implements Runnable, ThreadFactory {

    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;
    public static ViewBanHangBarcode Instance;
    public String maVach = "";
    private Webcam webcam = null;
    private WebcamPanel panel = null;
    private Executor ex = Executors.newSingleThreadExecutor(this);

    public ViewBanHangBarcode() {
        initComponents();
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLocationRelativeTo(null);

        initWebCam();
        Instance = this;
        txtKqKeyReleased(null);
        lblThanhTien.setText("0");
        changeSoLon();
        changeSoThung();
        closeForm();

    }

    public void closeForm() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                webcam.close();
            }
        });

    }

    @Override
    public void run() {
        do {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
            try {
                Result result = null;
                BufferedImage image = null;
                if (webcam.isOpen()) {
                    if ((image = webcam.getImage()) == null) {
                        continue;
                    }

                }

                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitMap = new BinaryBitmap(new HybridBinarizer(source));
                result = new MultiFormatReader().decode(bitMap);
                if (result != null) {
                    txtKq.setText(result.getText());

                }
            } catch (Exception e) {
                System.out.println("");
            }

        } while (true);
    }

    public void initWebCam() {
        try {
            Dimension size = WebcamResolution.QVGA.getSize();
            webcam = Webcam.getWebcams().get(0);
            webcam.setViewSize(size);
            panel = new WebcamPanel(webcam);
            panel.setPreferredSize(size);
            panel.setFPSDisplayed(true);
            pncam.add(panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 470, 300));
            ex.execute(this);
        } catch (Exception e) {
            System.out.println("");
        }

    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread();
        try {
            t = new Thread(r, "My Thread");
            t.setDaemon(true);

        } catch (Exception e) {
            System.out.println("");
        }
        return t;
    }

    public void changeSum() {
        String soThungString = jspSoThung.getValue() + "";
        int soThung = Integer.valueOf(soThungString);
        float giaThung = Float.valueOf(txtGiaThung.getText());
        String soLonString = jspSoLon.getValue() + "";
        int soLon = Integer.valueOf(soLonString);
        float giaLon = Float.valueOf(txtGiaLon.getText());
        float tong = 0;
        int soLonMThung = Integer.valueOf(lblSoLonMoiThung.getText());
        if (soLon < 0) {
            JOptionPane.showMessageDialog(this, "Số Lượng Không Hợp Lệ");
            jspSoLon.setValue(1);
            return;
        } else if (soThung < 0) {

            JOptionPane.showMessageDialog(this, "Số Lượng Không Hợp Lệ");
            jspSoThung.setValue(1);
            return;
        }

        if (soLon >= soLonMThung) {
            jspSoLon.setValue(0);
            jspSoThung.setValue(soThung + 1);

        }
        float soThungTrongKho = Float.valueOf(lblSoLuongThung.getText());
        if (soThung > soThungTrongKho) {
            JOptionPane.showMessageDialog(this, "Số Lượng Thùng Trong Kho Không Đủ");
            jspSoThung.setValue(soThung - 1);
            return;
        }
        float soLonTrongKho = Float.valueOf(lblSoLuongLon.getText());
        if (soThung == soThungTrongKho) {
            if (soLon > soLonTrongKho) {
                JOptionPane.showMessageDialog(this, "Số Lượng Lon Trong Kho Không Đủ");
                jspSoLon.setValue(soLon - 1);
                return;
            }

        }

        tong = (soThung * giaThung) + (soLon * giaLon);

        lblThanhTien.setText(tong + "");
    }

    public void soLuongCapNhat() {
        int soThungConLai = 0;
        int soLonConLai = 0;

        int soLonTrongKho = Integer.valueOf(lblSoLuongLon.getText());
        int soThungTrongKho = Integer.valueOf(lblSoLuongThung.getText());
        int soLonMThung = Integer.valueOf(lblSoLonMoiThung.getText());
        int soThungMua = Integer.valueOf(jspSoThung.getValue() + "");
        int soLonMua = Integer.valueOf(jspSoLon.getValue() + "");

        int temp = soLonTrongKho - soLonMua;
        if (temp < 0) {
            soLonConLai = soLonMThung + temp;
            soThungConLai = soThungTrongKho - 1 - soThungMua;
        } else if (temp >= 0) {
            soLonConLai = soLonTrongKho - soLonMua;
            soThungConLai = soThungTrongKho - soThungMua;
        }

        TrangChu.Instance.soLonConLai = soLonConLai + "";
        TrangChu.Instance.soThungConLai = soThungConLai + "";

    }

    public void changeSoThung() {
        jspSoThung.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                changeSum();
            }
        });
    }

    public void changeSoLon() {
        jspSoLon.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                changeSum();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        pncam = new javax.swing.JPanel();
        txtKq = new javax.swing.JTextField();
        lblTb = new javax.swing.JLabel();
        jspSoThung = new javax.swing.JSpinner();
        jspSoLon = new javax.swing.JSpinner();
        txtGiaThung = new javax.swing.JTextField();
        txtGiaLon = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnLogin = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblTenSp = new javax.swing.JLabel();
        lblThanhTien = new javax.swing.JLabel();
        lblSoLonMoiThung = new javax.swing.JLabel();
        lblMa = new javax.swing.JLabel();
        lblSoLuongLon = new javax.swing.JLabel();
        lblSoLuongThung = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(204, 255, 255));

        jPanel1.setBackground(new java.awt.Color(153, 255, 204));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pncam.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(pncam, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 450, 270));

        txtKq.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtKq.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKqKeyReleased(evt);
            }
        });
        jPanel1.add(txtKq, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 280, 240, 30));

        lblTb.setForeground(new java.awt.Color(204, 0, 51));
        lblTb.setText(" ");
        jPanel1.add(lblTb, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 310, 210, -1));

        jspSoThung.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jspSoThung.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jspSoThungStateChanged(evt);
            }
        });

        jspSoLon.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jspSoLon.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jspSoLonStateChanged(evt);
            }
        });

        txtGiaThung.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        txtGiaLon.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel1.setText("Số Thùng ");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel2.setText("Số Lon");

        btnLogin.setBackground(new java.awt.Color(204, 102, 0));
        btnLogin.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnLogin.setForeground(new java.awt.Color(255, 255, 255));
        btnLogin.setText("Thêm Vào Giỏ Hàng");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel3.setText("Giá Thùng ");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel4.setText("Giá Lon ");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel5.setText("Tên Sản Phẩm");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel6.setText("Thành Tiền");

        lblTenSp.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTenSp.setForeground(new java.awt.Color(255, 0, 51));
        lblTenSp.setText(" ");

        lblThanhTien.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N

        lblSoLonMoiThung.setFont(new java.awt.Font("Segoe UI", 0, 1)); // NOI18N

        lblMa.setFont(new java.awt.Font("Segoe UI", 0, 1)); // NOI18N

        lblSoLuongLon.setFont(new java.awt.Font("Segoe UI", 0, 1)); // NOI18N
        lblSoLuongLon.setForeground(new java.awt.Color(255, 255, 255));

        lblSoLuongThung.setFont(new java.awt.Font("Segoe UI", 0, 1)); // NOI18N
        lblSoLuongThung.setForeground(new java.awt.Color(255, 255, 255));
        lblSoLuongThung.setText("0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblTenSp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jspSoThung)
                    .addComponent(jspSoLon)
                    .addComponent(txtGiaThung)
                    .addComponent(txtGiaLon, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                    .addComponent(lblThanhTien, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblMa, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSoLuongLon, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblSoLonMoiThung, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblSoLuongThung, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lblTenSp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jspSoThung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jspSoLon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtGiaThung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtGiaLon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblThanhTien))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblMa)
                                .addGap(28, 28, 28)
                                .addComponent(lblSoLuongLon)))
                        .addContainerGap(24, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(lblSoLonMoiThung)
                        .addGap(24, 24, 24)
                        .addComponent(lblSoLuongThung)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jspSoThungStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jspSoThungStateChanged
        changeSoThung();
    }//GEN-LAST:event_jspSoThungStateChanged

    public void clear() {
        txtGiaLon.setText("0");
        txtGiaThung.setText("0");
        lblTenSp.setText("");
        lblThanhTien.setText("0");
        txtKq.setText("");
        jspSoLon.setValue(0);
        jspSoThung.setValue(0);
    }
    private void jspSoLonStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jspSoLonStateChanged
        changeSoLon();
    }//GEN-LAST:event_jspSoLonStateChanged

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        String soThung = String.valueOf(jspSoThung.getValue());
        String soLon = String.valueOf(jspSoLon.getValue());
        float soThungFL = Float.valueOf(soThung);
        float soLonFl = Float.valueOf(soLon);
        if (soLonFl < 1 && soThungFL < 1) {
            JOptionPane.showMessageDialog(this, "Nhập Số Lượng Để Thêm");
            return;

        }
        String tt = lblThanhTien.getText();
        TrangChu.Instance.jspGhSoThung = soThung;
        TrangChu.Instance.jspGhSoLon = soLon;
        TrangChu.Instance.lbGhThanhTien = tt;
        TrangChu.Instance.txtTenSpString = lblTenSp.getText();
        TrangChu.Instance.txtMaSpGioHang = lblMa.getText();
        soLuongCapNhat();
        TrangChu.Instance.hienThiGioHangBarcode();
        clear();
        JOptionPane.showMessageDialog(this, "Thêm Thành Công");
    }//GEN-LAST:event_btnLoginActionPerformed

    private void txtKqKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKqKeyReleased
        txtKq.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                GioHangBarcodeService sv = new GioHangBarcodeImpl();
                List<GioHangBarCodeViewModel> gh = sv.getList(txtKq.getText());
                if (gh.size() < 1) {
                    lblTb.setText("Không Tìm Thấy Sản Phẩm");

                } else {
                    for (GioHangBarCodeViewModel o : gh) {
                        lblTenSp.setText(o.getTenSp());
                        lblMa.setText(o.getMaSp());
                        lblSoLonMoiThung.setText(o.getSoLonMoiThung() + "");
                        txtGiaLon.setText(o.getGiaBanTheoLon() + "");
                        txtGiaThung.setText(o.getGiaBanTheoThung() + "");
                        System.out.println(o.getSoLuongLon());
                        lblSoLuongLon.setText(o.getSoLuongLon() + "");
                        lblSoLuongThung.setText(o.getSoLuongThung() + "");

                        lblTb.setText("");
                    }
                }
            }

            public void removeUpdate(DocumentEvent e) {

            }

            public void insertUpdate(DocumentEvent e) {
                GioHangBarcodeService sv = new GioHangBarcodeImpl();
                List<GioHangBarCodeViewModel> gh = sv.getList(txtKq.getText());
                if (gh.size() < 1) {
                    lblTb.setText("Không Tìm Thấy Sản Phẩm");

                } else {
                    for (GioHangBarCodeViewModel o : gh) {
                        lblTenSp.setText(o.getTenSp());
                        lblMa.setText(o.getMaSp());
                        lblSoLonMoiThung.setText(o.getSoLonMoiThung() + "");
                        txtGiaLon.setText(o.getGiaBanTheoLon() + "");
                        txtGiaThung.setText(o.getGiaBanTheoThung() + "");
                        lblTb.setText("");
                        lblSoLuongLon.setText(o.getSoLuongLon() + "");
                        lblSoLuongThung.setText(o.getSoLuongThung() + "");

                    }
                }
            }
        });

    }//GEN-LAST:event_txtKqKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ViewBanHangBarcode.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ViewBanHangBarcode.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ViewBanHangBarcode.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ViewBanHangBarcode.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ViewBanHangBarcode().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSpinner jspSoLon;
    private javax.swing.JSpinner jspSoThung;
    private javax.swing.JLabel lblMa;
    private javax.swing.JLabel lblSoLonMoiThung;
    private javax.swing.JLabel lblSoLuongLon;
    private javax.swing.JLabel lblSoLuongThung;
    private javax.swing.JLabel lblTb;
    private javax.swing.JLabel lblTenSp;
    private javax.swing.JLabel lblThanhTien;
    private javax.swing.JPanel pncam;
    private javax.swing.JTextField txtGiaLon;
    private javax.swing.JTextField txtGiaThung;
    private javax.swing.JTextField txtKq;
    // End of variables declaration//GEN-END:variables

}