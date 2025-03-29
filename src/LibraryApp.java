import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibraryApp extends JFrame {

    private static final String XML_FILE_PATH = "library_data.xml"; // Tên file XML lưu trữ

    private XmlDataService dataService;
    private List<Book> bookList;

    // Components GUI
    private JTextField txtIsbn;
    private JTextField txtTitle;
    private JTextField txtAuthor;
    private JTextField txtYear;
    private JTextField txtPublisher;
    private JTextField txtPages;
    private JTextField txtGenre;
    private JTextField txtPrice;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    private JTable bookTable;
    private DefaultTableModel tableModel;

    public LibraryApp() {
        // Khởi tạo Data Service
        try {
            dataService = new XmlDataService(XML_FILE_PATH);
        } catch (JAXBException e) {
            showError("Lỗi khởi tạo XML Service: " + e.getMessage());
            // Nếu không khởi tạo được, không thể tiếp tục
            System.exit(1);
            return; // Để trình biên dịch không báo lỗi
        }

        // Đọc dữ liệu ban đầu
        bookList = dataService.readBooks();
        if (bookList == null) { // Đảm bảo bookList không null
            bookList = new ArrayList<>();
        }

        // Cài đặt cửa sổ chính
        setTitle("Quản lý Thư viện Sách (XML)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600); // Tăng kích thước cửa sổ
        setLocationRelativeTo(null); // Hiển thị giữa màn hình

        // --- Tạo các components ---
        createInputPanel();
        createButtonPanel();
        createTablePanel();

        // --- Bố cục chính ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); // Khoảng cách giữa các thành phần
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Thêm padding

        JPanel formAndButtonsPanel = new JPanel(new BorderLayout(10, 5));
        formAndButtonsPanel.add(createInputPanel(), BorderLayout.CENTER);
        formAndButtonsPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        mainPanel.add(formAndButtonsPanel, BorderLayout.NORTH);
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);

        setContentPane(mainPanel);

        // --- Load dữ liệu vào bảng ---
        refreshTable();

        // --- Thêm Listeners ---
        setupActionListeners();
        setupTableSelectionListener();
    }

    // --- Phương thức tạo các phần của GUI ---

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin sách"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Khoảng cách giữa các ô
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Hàng 1: Mã sách (ISBN) & Tên sách
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Mã sách (ISBN):"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5; txtIsbn = new JTextField(15); inputPanel.add(txtIsbn, gbc);
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.0; inputPanel.add(new JLabel("Tên sách:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.5; txtTitle = new JTextField(20); inputPanel.add(txtTitle, gbc);

        // Hàng 2: Tác giả & Năm XB
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; inputPanel.add(new JLabel("Tác giả:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; txtAuthor = new JTextField(15); inputPanel.add(txtAuthor, gbc);
        gbc.gridx = 2; gbc.gridy = 1; inputPanel.add(new JLabel("Năm XB:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; txtYear = new JTextField(5); inputPanel.add(txtYear, gbc);

        // Hàng 3: Nhà XB & Số trang
        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Nhà xuất bản:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; txtPublisher = new JTextField(15); inputPanel.add(txtPublisher, gbc);
        gbc.gridx = 2; gbc.gridy = 2; inputPanel.add(new JLabel("Số trang:"), gbc);
        gbc.gridx = 3; gbc.gridy = 2; txtPages = new JTextField(5); inputPanel.add(txtPages, gbc);

        // Hàng 4: Thể loại & Giá sách
        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(new JLabel("Thể loại:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; txtGenre = new JTextField(15); inputPanel.add(txtGenre, gbc);
        gbc.gridx = 2; gbc.gridy = 3; inputPanel.add(new JLabel("Giá sách (VNĐ):"), gbc);
        gbc.gridx = 3; gbc.gridy = 3; txtPrice = new JTextField(10); inputPanel.add(txtPrice, gbc);

        // Reset weightx for labels if needed, though HORIZONTAL fill handles width
        gbc.weightx = 0.0;

        return inputPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Căn giữa, khoảng cách ngang 10, dọc 10
        btnAdd = new JButton("Thêm sách");
        btnUpdate = new JButton("Cập nhật");
        btnDelete = new JButton("Xóa sách");
        btnClear = new JButton("Làm mới"); // Nút xóa trắng form

        btnUpdate.setEnabled(false); // Vô hiệu hóa nút Sửa/Xóa ban đầu
        btnDelete.setEnabled(false);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        return buttonPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"Mã sách", "Tên sách", "Tác giả", "Năm XB", "Nhà XB", "Số trang", "Thể loại", "Giá (VNĐ)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            // Ngăn không cho sửa trực tiếp trên bảng
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Chỉ cho chọn 1 hàng
        bookTable.setFillsViewportHeight(true); // Đảm bảo bảng chiếm hết chiều cao của ScrollPane
        // Thiết lập độ rộng cột (tùy chọn)
        bookTable.getColumnModel().getColumn(0).setPreferredWidth(100); // ISBN
        bookTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Title
        bookTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Author
        // ... điều chỉnh các cột khác nếu cần

        return new JScrollPane(bookTable); // Đặt bảng vào trong JScrollPane
    }

    // --- Phương thức xử lý logic ---

    private void refreshTable() {
        // Xóa dữ liệu cũ
        tableModel.setRowCount(0);
        // Thêm dữ liệu mới từ bookList
        for (Book book : bookList) {
            tableModel.addRow(new Object[]{
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublicationYear(),
                    book.getPublisher(),
                    book.getPages(),
                    book.getGenre(),
                    String.format("%,.0f", book.getPrice()) // Định dạng giá tiền
            });
        }
        // Sau khi làm mới bảng, không có hàng nào được chọn -> vô hiệu hóa nút Sửa/Xóa
        disableEditDeleteButtons();
        clearInputFields(); // Xóa trắng form luôn
    }

    private void setupActionListeners() {
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBook();
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBook();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBook();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearInputFields();
                bookTable.clearSelection(); // Bỏ chọn hàng trên bảng
                disableEditDeleteButtons();
            }
        });
    }

    private void setupTableSelectionListener() {
        bookTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Kiểm tra nếu việc chọn đang diễn ra và đã kết thúc
                if (!e.getValueIsAdjusting() && bookTable.getSelectedRow() != -1) {
                    int selectedRow = bookTable.convertRowIndexToModel(bookTable.getSelectedRow());
                    populateFieldsFromSelectedRow(selectedRow);
                    enableEditDeleteButtons(); // Cho phép sửa/xóa khi có dòng được chọn
                } else if (bookTable.getSelectedRow() == -1) {
                    // Nếu không có dòng nào được chọn (ví dụ sau khi xóa)
                    disableEditDeleteButtons();
                }
            }
        });
    }

    private void populateFieldsFromSelectedRow(int modelRowIndex) {
        if (modelRowIndex >= 0 && modelRowIndex < bookList.size()) {
            Book selectedBook = bookList.get(modelRowIndex);
            txtIsbn.setText(selectedBook.getIsbn());
            txtTitle.setText(selectedBook.getTitle());
            txtAuthor.setText(selectedBook.getAuthor());
            txtYear.setText(String.valueOf(selectedBook.getPublicationYear()));
            txtPublisher.setText(selectedBook.getPublisher());
            txtPages.setText(String.valueOf(selectedBook.getPages()));
            txtGenre.setText(selectedBook.getGenre());
            txtPrice.setText(String.valueOf(selectedBook.getPrice())); // Không cần định dạng khi điền vào ô text

            txtIsbn.setEditable(false); // Không cho phép sửa ISBN khi cập nhật
        }
    }

    private void clearInputFields() {
        txtIsbn.setText("");
        txtTitle.setText("");
        txtAuthor.setText("");
        txtYear.setText("");
        txtPublisher.setText("");
        txtPages.setText("");
        txtGenre.setText("");
        txtPrice.setText("");
        txtIsbn.setEditable(true); // Cho phép nhập ISBN trở lại
    }

    private void enableEditDeleteButtons() {
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
    }

    private void disableEditDeleteButtons() {
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    // --- Phương thức CRUD ---

    private void addBook() {
        // 1. Đọc dữ liệu từ các ô input
        String isbn = txtIsbn.getText().trim();
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String yearStr = txtYear.getText().trim();
        String publisher = txtPublisher.getText().trim();
        String pagesStr = txtPages.getText().trim();
        String genre = txtGenre.getText().trim();
        String priceStr = txtPrice.getText().trim();

        // 2. Validate dữ liệu
        if (isbn.isEmpty() || title.isEmpty() || author.isEmpty() || yearStr.isEmpty() ||
                publisher.isEmpty() || pagesStr.isEmpty() || genre.isEmpty() || priceStr.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin sách.");
            return;
        }

        int year, pages;
        double price;
        try {
            year = Integer.parseInt(yearStr);
            pages = Integer.parseInt(pagesStr);
            price = Double.parseDouble(priceStr);
            if (year <= 0 || pages <= 0 || price < 0) {
                showError("Năm xuất bản, số trang phải là số dương. Giá sách không được âm.");
                return;
            }
        } catch (NumberFormatException ex) {
            showError("Năm xuất bản, số trang, giá sách phải là số hợp lệ.");
            return;
        }

        // Kiểm tra ISBN trùng lặp
        if (findBookByIsbn(isbn).isPresent()) {
            showError("Mã sách (ISBN) '" + isbn + "' đã tồn tại.");
            return;
        }

        // 3. Tạo đối tượng Book mới
        Book newBook = new Book(isbn, title, author, year, publisher, pages, genre, price);

        // 4. Thêm vào danh sách trong bộ nhớ
        bookList.add(newBook);

        // 5. Lưu vào file XML
        dataService.writeBooks(bookList);

        // 6. Cập nhật bảng và xóa trắng form
        refreshTable();
        showMessage("Thêm sách thành công!");
    }

    private void updateBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Vui lòng chọn một cuốn sách để cập nhật.");
            return;
        }
        int modelRowIndex = bookTable.convertRowIndexToModel(selectedRow);

        // Lấy ISBN từ ô input (đã bị disable edit) hoặc từ dòng được chọn
        String isbn = txtIsbn.getText().trim(); // ISBN không đổi

        // 1. Đọc dữ liệu cập nhật từ các ô input
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String yearStr = txtYear.getText().trim();
        String publisher = txtPublisher.getText().trim();
        String pagesStr = txtPages.getText().trim();
        String genre = txtGenre.getText().trim();
        String priceStr = txtPrice.getText().trim();

        // 2. Validate dữ liệu (tương tự như Add)
        if (title.isEmpty() || author.isEmpty() || yearStr.isEmpty() ||
                publisher.isEmpty() || pagesStr.isEmpty() || genre.isEmpty() || priceStr.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin sách.");
            return;
        }
        int year, pages;
        double price;
        try {
            year = Integer.parseInt(yearStr);
            pages = Integer.parseInt(pagesStr);
            price = Double.parseDouble(priceStr);
            if (year <= 0 || pages <= 0 || price < 0) {
                showError("Năm xuất bản, số trang phải là số dương. Giá sách không được âm.");
                return;
            }
        } catch (NumberFormatException ex) {
            showError("Năm xuất bản, số trang, giá sách phải là số hợp lệ.");
            return;
        }

        // 3. Tìm sách trong danh sách và cập nhật
        Optional<Book> bookToUpdateOpt = findBookByIsbn(isbn);
        if (bookToUpdateOpt.isPresent()) {
            Book bookToUpdate = bookToUpdateOpt.get();
            bookToUpdate.setTitle(title);
            bookToUpdate.setAuthor(author);
            bookToUpdate.setPublicationYear(year);
            bookToUpdate.setPublisher(publisher);
            bookToUpdate.setPages(pages);
            bookToUpdate.setGenre(genre);
            bookToUpdate.setPrice(price);

            // 4. Lưu vào file XML
            dataService.writeBooks(bookList);

            // 5. Cập nhật bảng và xóa trắng form
            refreshTable();
            showMessage("Cập nhật sách thành công!");
        } else {
            showError("Không tìm thấy sách với mã ISBN: " + isbn + " để cập nhật."); // Lỗi logic nếu xảy ra
        }
        // Bỏ chọn hàng sau khi cập nhật
        bookTable.clearSelection();
    }

    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Vui lòng chọn một cuốn sách để xóa.");
            return;
        }
        int modelRowIndex = bookTable.convertRowIndexToModel(selectedRow);

        // Lấy ISBN của sách cần xóa (từ dòng được chọn)
        String isbnToDelete = (String) tableModel.getValueAt(modelRowIndex, 0);

        // Xác nhận xóa
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa sách có mã '" + isbnToDelete + "'?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            // 1. Tìm và xóa sách khỏi danh sách trong bộ nhớ
            boolean removed = bookList.removeIf(book -> book.getIsbn().equals(isbnToDelete));

            if (removed) {
                // 2. Lưu danh sách mới vào file XML
                dataService.writeBooks(bookList);

                // 3. Cập nhật bảng và xóa trắng form
                refreshTable(); // refreshTable sẽ tự động clear form và disable nút
                showMessage("Xóa sách thành công!");
            } else {
                showError("Không tìm thấy sách để xóa (lỗi logic).");
            }
        }
        // Bỏ chọn hàng sau khi xóa (hoặc hủy xóa)
        bookTable.clearSelection();
    }

    // --- Helper methods ---

    private Optional<Book> findBookByIsbn(String isbn) {
        return bookList.stream()
                .filter(book -> book.getIsbn().equalsIgnoreCase(isbn))
                .findFirst();
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }


    // --- Main method ---
    public static void main(String[] args) {
        // Chạy ứng dụng trên Event Dispatch Thread (EDT) của Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LibraryApp().setVisible(true);
            }
        });
    }
}
