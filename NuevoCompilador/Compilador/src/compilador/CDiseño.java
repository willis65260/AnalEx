/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package compilador;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import static java.awt.SystemColor.text;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author elalb
 */

public class CDiseño extends javax.swing.JFrame {

    public class LineNumberingTextArea extends JTextArea {
        private JTextArea textArea;
        private int lastLineCount;

        public LineNumberingTextArea(JTextArea textArea) {
            this.textArea = textArea;
            setEditable(false);
            setFont(textArea.getFont());
            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));
            setForeground(Color.GRAY);
            setMargin(new Insets(0, 5, 0, 5));
            updateLineNumbers();
            textArea.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    updateLineNumbers();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    updateLineNumbers();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    updateLineNumbers();
                }
            });
        }

        public int getPreferredWidth() {
            return getFontMetrics(getFont()).stringWidth(String.valueOf(lastLineCount)) + 20;
        }

        public int getPreferredHeight() {
            return textArea.getHeight();
        }

        public void updateLineNumbers() {
            int lineCount = textArea.getLineCount();
            if (lineCount != lastLineCount) {
                lastLineCount = lineCount;
                setPreferredSize(new Dimension(getPreferredWidth(), getPreferredHeight()));
                invalidate();
                repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int lineHeight = textArea.getFontMetrics(textArea.getFont()).getHeight();
            int lineCount = Math.min(getHeight() / lineHeight, lastLineCount); // Obtener el número correcto de líneas a
                                                                               // mostrar
            int y = getInsets().top + lineHeight;
            for (int i = 1; i <= lineCount; i++) {
                String lineText = String.valueOf(i);
                int x = getPreferredWidth() - getInsets().right - getFontMetrics(getFont()).stringWidth(lineText);
                g.drawString(lineText, x, y);
                y += lineHeight;
            }
        }
    }

    public CDiseño() {
        initComponents();

        newTab();

        // setIconImage(Toolkit.getDefaultToolkit()
        // .getImage(getClass().getResource("/analEX/NuevoCompilador/Compilador/src/img/logo.png")));

        // Obtener el tamaño de la pantalla
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();

        // Obtener el tamaño de la ventana
        Dimension ventana = getSize();

        // Calcular la posición central
        int posX = (pantalla.width - ventana.width) / 2;
        int posY = (pantalla.height - ventana.height) / 2;

        // Establecer la posición de la ventana
        setLocation(posX, posY);

        this.setTitle("SyntaxMaestro");

    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto", "txt"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                JTextArea textArea = new JTextArea();
                textArea.setText(content.toString());

                JScrollPane scrollPane = new JScrollPane(textArea);
                LineNumberingTextArea lineNumberingTextArea = new LineNumberingTextArea(textArea);
                scrollPane.setRowHeaderView(lineNumberingTextArea);

                JPanel newPanel = new JPanel();
                newPanel.setLayout(new BorderLayout());
                newPanel.add(scrollPane, BorderLayout.CENTER);

                String title = file.getName();
                cfPanel.addTab(title, newPanel);
                int index = cfPanel.indexOfComponent(newPanel);
                cfPanel.setSelectedIndex(index);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private String getTextFromSelectedTab() {
        int index = cfPanel.getSelectedIndex();
        if (index != -1) {
            Component component = cfPanel.getComponentAt(index);
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                Component[] components = panel.getComponents();
                for (Component comp : components) {
                    if (comp instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) comp;
                        Component view = scrollPane.getViewport().getView();
                        if (view instanceof JTextArea) {
                            JTextArea textArea = (JTextArea) view;
                            return textArea.getText();
                        }
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No hay una pestaña activa", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    private void saveFile() {
        int index = cfPanel.getSelectedIndex();
        if (index != -1) {
            Component component = cfPanel.getComponentAt(index);
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                Component[] components = panel.getComponents();
                for (Component comp : components) {
                    if (comp instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) comp;
                        Component view = scrollPane.getViewport().getView();
                        if (view instanceof JTextArea) {
                            JTextArea textArea = (JTextArea) view;
                            String content = textArea.getText();

                            JFileChooser fileChooser = new JFileChooser();
                            fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto", "txt"));
                            int result = fileChooser.showSaveDialog(this);
                            if (result == JFileChooser.APPROVE_OPTION) {
                                File file = fileChooser.getSelectedFile();
                                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                                    writer.write(content);
                                    JOptionPane.showMessageDialog(this, "Archivo guardado exitosamente", "Guardado",
                                            JOptionPane.INFORMATION_MESSAGE);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                    JOptionPane.showMessageDialog(this, "Error al guardar el archivo", "Error",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No hay una pestaña activa", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void closeTabContent() {
        int index = cfPanel.getSelectedIndex();
        if (index != -1) {
            Component component = cfPanel.getComponentAt(index);
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                Component[] components = panel.getComponents();
                for (Component comp : components) {
                    if (comp instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) comp;
                        Component view = scrollPane.getViewport().getView();
                        if (view instanceof JTextArea) {
                            JTextArea textArea = (JTextArea) view;
                            textArea.setText(""); // Limpiar el contenido del JTextArea
                            break; // Detener el bucle
                        }
                    }
                }
            }
        }
    }

    private void newTab() {
        JTextArea newTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(newTextArea);
        LineNumberingTextArea lineNumberingTextArea = new LineNumberingTextArea(newTextArea);
        scrollPane.setRowHeaderView(lineNumberingTextArea);
        JPanel newPanel = new JPanel();
        newPanel.setLayout(new BorderLayout());
        newPanel.add(scrollPane, BorderLayout.CENTER);
        String title = "NuevoArchivo" + (cfPanel.getTabCount() + 1);
        cfPanel.addTab(title, newPanel);
        int index = cfPanel.indexOfComponent(newPanel);
        cfPanel.setSelectedIndex(index);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cfPanel = new javax.swing.JTabbedPane();
        cerrarBtn = new javax.swing.JButton();
        nuearchBtn = new javax.swing.JButton();
        abrirBtn = new javax.swing.JButton();
        guardarBtn = new javax.swing.JButton();
        borrarBtn = new javax.swing.JButton();
        compiBtn = new javax.swing.JButton();
        lexLbl = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lexTarea = new javax.swing.JLabel();
        sinLbl = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        sinTarea = new javax.swing.JLabel();
        barraMenu = new javax.swing.JMenuBar();
        archMen = new javax.swing.JMenu();
        nvoArchMenu = new javax.swing.JMenuItem();
        abrArchMenu = new javax.swing.JMenuItem();
        grdArchMenu = new javax.swing.JMenuItem();
        brcArchMenu = new javax.swing.JMenuItem();
        anaMenu = new javax.swing.JMenu();
        infMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(new java.awt.Dimension(0, 0));

        cfPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        // cerrarBtn.setIcon(new
        // javax.swing.ImageIcon(getClass().getResource("/analEX/NuevoCompilador/Compilador/src/img/cerrar.png")));
        // // NOI18N
        cerrarBtn.setToolTipText("Cerrar pestaña activa");
        cerrarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cerrarBtnActionPerformed(evt);
            }
        });

        // nuearchBtn.setIcon(new javax.swing.ImageIcon(
        // getClass().getResource("/analEX/NuevoCompilador/Compilador/src/img/nuearch.png")));
        // // NOI18N
        nuearchBtn.setToolTipText("Nuevo archivo");
        nuearchBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuearchBtnActionPerformed(evt);
            }
        });

        // abrirBtn.setIcon(new javax.swing.ImageIcon(
        // getClass().getResource("/analEX/NuevoCompilador/Compilador/src/img/abrir.png")));
        // // NOI18N
        abrirBtn.setToolTipText("Abrir archivo");
        abrirBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abrirBtnActionPerformed(evt);
            }
        });

        // guardarBtn.setIcon(new javax.swing.ImageIcon(
        // getClass().getResource("/analEX/NuevoCompilador/Compilador/src/img/guardar.png")));
        // // NOI18N
        guardarBtn.setToolTipText("Guardar archivo");
        guardarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarBtnActionPerformed(evt);
            }
        });

        // borrarBtn.setIcon(new javax.swing.ImageIcon(
        // getClass().getResource("/analEX/NuevoCompilador/Compilador/src/img/borrar.png")));
        // // NOI18N
        borrarBtn.setToolTipText("Borrar contenido");
        borrarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                borrarBtnActionPerformed(evt);
            }
        });

        // compiBtn.setIcon(new javax.swing.ImageIcon(
        // getClass().getResource("/analEX/NuevoCompilador/Compilador/src/img/compil.png")));
        // // NOI18N
        compiBtn.setToolTipText("Analizar");
        compiBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compiBtnActionPerformed(evt);
            }
        });

        lexLbl.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lexLbl.setText("Analisis Lexico");

        // lexTarea.setEditable(false);
        // lexTarea.setColumns(20);
        // lexTarea.setRows(5);
        jScrollPane1.setViewportView(lexTarea);

        sinLbl.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        sinLbl.setText("Analisis Sintactico");

        // sinTarea.setEditable(false);
        // sinTarea.setColumns(20);
        // sinTarea.setRows(5);
        jScrollPane2.setViewportView(sinTarea);

        archMen.setText("Archivo");
        archMen.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        nvoArchMenu.setText("Nuevo archivo");
        nvoArchMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nvoArchMenuActionPerformed(evt);
            }
        });
        archMen.add(nvoArchMenu);

        abrArchMenu.setText("Abrir archivo");
        abrArchMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abrArchMenuActionPerformed(evt);
            }
        });
        archMen.add(abrArchMenu);

        grdArchMenu.setText("Guardar archivo");
        grdArchMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grdArchMenuActionPerformed(evt);
            }
        });
        archMen.add(grdArchMenu);

        brcArchMenu.setText("Borrar contenido archivo");
        brcArchMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                brcArchMenuActionPerformed(evt);
            }
        });
        archMen.add(brcArchMenu);

        barraMenu.add(archMen);

        anaMenu.setText("Analizar");
        anaMenu.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        barraMenu.add(anaMenu);

        infMenu.setText("Informacion");
        infMenu.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        barraMenu.add(infMenu);

        setJMenuBar(barraMenu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(cfPanel, javax.swing.GroupLayout.Alignment.LEADING,
                                                javax.swing.GroupLayout.PREFERRED_SIZE, 640,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout
                                                .createSequentialGroup()
                                                .addComponent(nuearchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(abrirBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(guardarBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(borrarBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(compiBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(484, 484, 484)
                                                .addComponent(cerrarBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(lexLbl)
                                        .addComponent(sinLbl)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 605,
                                                Short.MAX_VALUE)
                                        .addComponent(jScrollPane1))
                                .addContainerGap(17, Short.MAX_VALUE)));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(cerrarBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(nuearchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(abrirBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(guardarBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(borrarBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(compiBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(lexLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cfPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 680,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 318,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(sinLbl)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 318,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap()))));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cerrarBtnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cerrarBtnActionPerformed
        int index = cfPanel.getSelectedIndex();
        if (index != -1) {
            cfPanel.removeTabAt(index);
        }

    }// GEN-LAST:event_cerrarBtnActionPerformed

    private void nuearchBtnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_nuearchBtnActionPerformed
        newTab();
    }// GEN-LAST:event_nuearchBtnActionPerformed

    private void abrirBtnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_abrirBtnActionPerformed
        openFile();
    }// GEN-LAST:event_abrirBtnActionPerformed

    private void guardarBtnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_guardarBtnActionPerformed
        saveFile();
    }// GEN-LAST:event_guardarBtnActionPerformed

    private void compiBtnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_compiBtnActionPerformed
        // Llama al método getTextFromSelectedTab() para obtener el texto del JTextArea
        String texto = getTextFromSelectedTab();
        Analizador ana = new Analizador(texto);
        String tablaSintactico = "<html> <table class=\"table\">" +
                "<thead>" +
                "<tr>" +
                "<th scope=\"col\">Lexema</th>" +
                "<th scope=\"col\">pila</th>" +
                "<th scope=\"col\">accion</th>" +
                "<th scope=\"col\">palabra</th>" +
                "</tr>" +
                "</thead>" +
                "<tbody>";

        String tablaLexico = "<html> <table class=\"table\">" +
                "<thead>" +
                "<tr>" +
                "<th scope=\"col\">palabra</th>" +
                "<th scope=\"col\">lexema</th>" +
                "</tr>" +
                "</thead>" +
                "<tbody>";

        String[] analiz = ana.Sintactico();

        tablaSintactico += analiz[0];

        tablaSintactico += "</tbody>" +
                "</table></html>";

        tablaLexico += analiz[1];
        tablaLexico += "</tbody>" +
                "</table></html>";
        lexTarea.setText(tablaSintactico);
        sinTarea.setText(tablaLexico);
        // Haz algo con el texto obtenido, como mostrarlo en un cuadro de diálogo
        // JOptionPane.showMessageDialog(this, "Texto del JTextArea: " + texto);
    }// GEN-LAST:event_compiBtnActionPerformed

    private void borrarBtnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_borrarBtnActionPerformed
        closeTabContent();
    }// GEN-LAST:event_borrarBtnActionPerformed

    private void nvoArchMenuActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_nvoArchMenuActionPerformed
        newTab();
    }// GEN-LAST:event_nvoArchMenuActionPerformed

    private void grdArchMenuActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_grdArchMenuActionPerformed
        saveFile();
    }// GEN-LAST:event_grdArchMenuActionPerformed

    private void abrArchMenuActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_abrArchMenuActionPerformed
        openFile();
    }// GEN-LAST:event_abrArchMenuActionPerformed

    private void brcArchMenuActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_brcArchMenuActionPerformed
        closeTabContent();
    }// GEN-LAST:event_brcArchMenuActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
        // (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the default
         * look and feel.
         * For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CDiseño.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CDiseño.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CDiseño.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CDiseño.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CDiseño().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem abrArchMenu;
    private javax.swing.JButton abrirBtn;
    private javax.swing.JMenu anaMenu;
    private javax.swing.JMenu archMen;
    private javax.swing.JMenuBar barraMenu;
    private javax.swing.JButton borrarBtn;
    private javax.swing.JMenuItem brcArchMenu;
    private javax.swing.JButton cerrarBtn;
    private javax.swing.JTabbedPane cfPanel;
    private javax.swing.JButton compiBtn;
    private javax.swing.JMenuItem grdArchMenu;
    private javax.swing.JButton guardarBtn;
    private javax.swing.JMenu infMenu;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lexLbl;
    private javax.swing.JLabel lexTarea;
    private javax.swing.JButton nuearchBtn;
    private javax.swing.JMenuItem nvoArchMenu;
    private javax.swing.JLabel sinLbl;
    private javax.swing.JLabel sinTarea;
    // End of variables declaration//GEN-END:variables
}
