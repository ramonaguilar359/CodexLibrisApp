public class LoanRequest {
    private String loan_date;
    private String due_date;
    private String return_date;
    private int userId;
    private int bookId;
    private int statusId;

    public LoanRequest(String loan_date, String due_date, String return_date, int userId, int bookId, int statusId) {
        this.loan_date = loan_date;
        this.due_date = due_date;
        this.return_date = return_date;
        this.userId = userId;
        this.bookId = bookId;
        this.statusId = statusId;
    }
}
