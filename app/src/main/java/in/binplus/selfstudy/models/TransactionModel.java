package in.binplus.selfstudy.models;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 30,January,2020
 */
public class TransactionModel {

    String id,trans_id,trans_image,status,trans_date,trans_amount;

    public TransactionModel() {
    }

    public TransactionModel(String id, String trans_id, String trans_image, String status, String trans_date, String trans_amount) {
        this.id = id;
        this.trans_id = trans_id;
        this.trans_image = trans_image;
        this.status = status;
        this.trans_date = trans_date;
        this.trans_amount = trans_amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrans_id() {
        return trans_id;
    }

    public void setTrans_id(String trans_id) {
        this.trans_id = trans_id;
    }

    public String getTrans_image() {
        return trans_image;
    }

    public void setTrans_image(String trans_image) {
        this.trans_image = trans_image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTrans_date() {
        return trans_date;
    }

    public void setTrans_date(String trans_date) {
        this.trans_date = trans_date;
    }

    public String getTrans_amount() {
        return trans_amount;
    }

    public void setTrans_amount(String trans_amount) {
        this.trans_amount = trans_amount;
    }
}
