package com.store.app.petstore.Controllers.Admin;

import com.store.app.petstore.Controllers.ControllerUtils;
import com.store.app.petstore.DAO.DiscountDAO;
import com.store.app.petstore.Models.Entities.Discount;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class DiscountInforController implements Initializable {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnFix;

    @FXML
    private Button btnSave;

    @FXML
    private ChoiceBox<String> cbType;

    @FXML
    private FontAwesomeIconView closeIcon;

    @FXML
    private AnchorPane discountInforPopup;

    @FXML
    private VBox discountPopUp;

    @FXML
    private DatePicker dpEndDate;

    @FXML
    private DatePicker dpStartDate;

    @FXML
    private TextField txtCode;

    @FXML
    private TextField txtDiscountId;

    @FXML
    private TextField txtMaxDiscountValue;

    @FXML
    private TextField txtMinOrderValue;

    @FXML
    private TextField txtDiscountValue;

    private int idDiscountCurrent;
    private boolean isNewDiscount = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTypeChoiceBox();
        setupEventHandlers();
        setupInitialState();
    }

    private void setupTypeChoiceBox() {
        cbType.getItems().addAll("Phần trăm", "Cố định");
        cbType.setValue("Phần trăm");

        cbType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                boolean isFixed = newValue.equals("Cố định");
                txtMaxDiscountValue.setDisable(isFixed);

                if (isFixed) {
                    txtMaxDiscountValue.clear();
                }
            }
        });
    }

    private void setupEventHandlers() {
        btnAdd.setOnAction(event -> handleAdd());
        btnFix.setOnAction(event -> handleFix());
        btnSave.setOnAction(event -> handleSave());
        btnDelete.setOnAction(event -> handleDelete());
        closeIcon.setOnMouseClicked(event -> closeWindow());
    }

    private void setupInitialState() {
        btnAdd.setDisable(false);
        btnFix.setDisable(true);
        btnDelete.setDisable(true);
        btnSave.setDisable(true);

        disableEditing();
    }

    private void disableEditing() {
        txtDiscountId.setDisable(true);
        txtCode.setDisable(true);
        cbType.setDisable(true);
        txtDiscountValue.setDisable(true);
        txtMaxDiscountValue.setDisable(true);
        txtMinOrderValue.setDisable(true);
        dpStartDate.setDisable(true);
        dpEndDate.setDisable(true);
    }

    private void enableEditing() {
        txtCode.setDisable(false);
        cbType.setDisable(false);
        txtDiscountValue.setDisable(false);
        txtMinOrderValue.setDisable(false);
        dpStartDate.setDisable(false);
        dpEndDate.setDisable(false);

        boolean isFixed = cbType.getValue().equals("Cố định");
        txtMaxDiscountValue.setDisable(isFixed);
    }

    private void clearFields() {
        txtDiscountId.clear();
        txtCode.clear();
        txtDiscountValue.clear();
        txtMaxDiscountValue.clear();
        txtMinOrderValue.clear();
        dpStartDate.setValue(LocalDate.now());
        dpEndDate.setValue(LocalDate.now().plusDays(30));
        cbType.setValue("Phần trăm");
    }

    private void closeWindow() {
        Stage stage = (Stage) discountInforPopup.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    private void handleAdd() {
        clearFields();
        enableEditing();
        isNewDiscount = true;

        btnAdd.setDisable(true);
        btnFix.setDisable(true);
        btnDelete.setDisable(true);
        btnSave.setDisable(false);

        int nextId = getNextDiscountId();
        txtDiscountId.setText(String.valueOf(nextId));
        idDiscountCurrent = nextId;
    }

    private int getNextDiscountId() {
        ArrayList<Discount> discounts = DiscountDAO.findAll();
        if (discounts == null || discounts.isEmpty()) {
            return 1;
        }
        return discounts.stream()
                .mapToInt(Discount::getDiscountId)
                .max()
                .orElse(0) + 1;
    }

    private void handleFix() {
        enableEditing();
        btnFix.setDisable(true);
        btnAdd.setDisable(true);
        btnDelete.setDisable(true);
        btnSave.setDisable(false);
    }

    private void handleDelete() {
        if (idDiscountCurrent > 0) {
            if (ControllerUtils.showConfirmationAndWait("Xác nhận xóa khuyến mãi", "Bạn có chắc chắn muốn xóa khuyến mãi này?")) {
                int result = DiscountDAO.delete(idDiscountCurrent);

                if (result > 0) {
                    ControllerUtils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xóa khuyến mãi thành công!");
                    clearFields();
                    setupInitialState();
                } else {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa khuyến mãi!");
                }
            }
        }
    }

    private void handleSave() {
        if (validateInput()) {
            try {
                Discount discount = new Discount();

                if (!isNewDiscount) {
                    discount.setDiscountId(idDiscountCurrent);
                }

                discount.setCode(txtCode.getText().trim());

                // Convert discount type from Vietnamese to English
                String discountType = cbType.getValue().equals("Phần trăm") ? "percent" : "fixed";
                discount.setDiscountType(discountType);

                double value = Double.parseDouble(txtDiscountValue.getText().trim());
                discount.setValue(value);

                discount.setStartDate(dpStartDate.getValue());
                discount.setEndDate(dpEndDate.getValue());

                double minOrderValue = 0;
                if (!txtMinOrderValue.getText().trim().isEmpty()) {
                    minOrderValue = Double.parseDouble(txtMinOrderValue.getText().trim());
                }
                discount.setMinOrderValue(minOrderValue);

                double maxDiscountValue = 0;
                if (!txtMaxDiscountValue.getText().trim().isEmpty() && !discountType.equals("fixed")) {
                    maxDiscountValue = Double.parseDouble(txtMaxDiscountValue.getText().trim());
                }
                discount.setMaxDiscountValue(maxDiscountValue);

                int result;
                if (isNewDiscount) {
                    // Check for duplicate code
                    if (DiscountDAO.findByCode(discount.getCode()) != null) {
                        ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Mã khuyến mãi đã tồn tại!");
                        return;
                    }
                    result = DiscountDAO.insert(discount);
                } else {
                    // Check for duplicate code, excluding the current discount
                    Discount existingDiscount = DiscountDAO.findByCode(discount.getCode());
                    if (existingDiscount != null && existingDiscount.getDiscountId() != idDiscountCurrent) {
                        ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Mã khuyến mãi đã tồn tại!");
                        return;
                    }
                    result = DiscountDAO.update(discount);
                }

                if (result > 0) {
                    ControllerUtils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Lưu thông tin khuyến mãi thành công!");
                    disableEditing();
                    btnFix.setDisable(false);
                    btnDelete.setDisable(false);
                    btnAdd.setDisable(false);
                    btnSave.setDisable(true);
                    isNewDiscount = false;
                } else {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu thông tin khuyến mãi!");
                }
            } catch (NumberFormatException e) {
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Giá trị không hợp lệ. Vui lòng nhập số!");
            } catch (Exception e) {
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Đã xảy ra lỗi: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private boolean validateInput() {
        if (txtCode.getText().trim().isEmpty()) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập mã khuyến mãi!");
            return false;
        }

        if (txtDiscountValue.getText().trim().isEmpty()) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập giá trị giảm giá!");
            return false;
        }

        if (cbType.getValue() == null) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn loại khuyến mãi!");
            return false;
        }

        if (dpStartDate.getValue() == null) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn ngày bắt đầu!");
            return false;
        }

        if (dpEndDate.getValue() == null) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn ngày kết thúc!");
            return false;
        }

        if (dpEndDate.getValue().isBefore(dpStartDate.getValue())) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Ngày kết thúc phải sau ngày bắt đầu!");
            return false;
        }

        try {
            if (!txtDiscountValue.getText().trim().isEmpty()) {
                double value = Double.parseDouble(txtDiscountValue.getText().trim());
                if (value <= 0) {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Giá trị giảm giá phải lớn hơn 0!");
                    return false;
                }

                // If percent type, check that value is <= 100
                if (cbType.getValue().equals("Phần trăm") && value > 100) {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Giá trị phần trăm không thể lớn hơn 100!");
                    return false;
                }
            }

            if (!txtMinOrderValue.getText().trim().isEmpty()) {
                double minOrderValue = Double.parseDouble(txtMinOrderValue.getText().trim());
                if (minOrderValue < 0) {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Giá trị đơn hàng tối thiểu không thể âm!");
                    return false;
                }
            }

            if (!txtMaxDiscountValue.getText().trim().isEmpty() && !cbType.getValue().equals("Cố định")) {
                double maxDiscountValue = Double.parseDouble(txtMaxDiscountValue.getText().trim());
                if (maxDiscountValue <= 0) {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Giá trị giảm tối đa phải lớn hơn 0!");
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Giá trị không hợp lệ. Vui lòng nhập số!");
            return false;
        }

        return true;
    }

    public void setDiscount(Discount discount) {
        if (discount == null) return;

        isNewDiscount = false;
        idDiscountCurrent = discount.getDiscountId();

        txtDiscountId.setText(String.valueOf(discount.getDiscountId()));
        txtCode.setText(discount.getCode());

        // Convert discount type from English to Vietnamese
        String displayType = discount.getDiscountType().equals("percent") ? "Phần trăm" : "Cố định";
        cbType.setValue(displayType);

        txtDiscountValue.setText(String.valueOf(discount.getValue()));
        txtMinOrderValue.setText(String.valueOf(discount.getMinOrderValue()));

        // Only set max discount value if not fixed type
        if (!discount.getDiscountType().equals("fixed")) {
            txtMaxDiscountValue.setText(String.valueOf(discount.getMaxDiscountValue()));
        } else {
            txtMaxDiscountValue.clear();
            txtMaxDiscountValue.setDisable(true);
        }

        dpStartDate.setValue(discount.getStartDate());
        dpEndDate.setValue(discount.getEndDate());

        btnAdd.setDisable(false);
        btnFix.setDisable(false);
        btnDelete.setDisable(false);
        btnSave.setDisable(true);
    }
}
