import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlDataService {

    private final String xmlFilePath;
    private final JAXBContext context;

    public XmlDataService(String xmlFilePath) throws JAXBException {
        this.xmlFilePath = xmlFilePath;
        // Khởi tạo JAXB context cho các lớp Library và Book
        this.context = JAXBContext.newInstance(Library.class, Book.class);
    }

    /**
     * Đọc danh sách sách từ file XML.
     * Nếu file không tồn tại, trả về danh sách rỗng.
     *
     * @return Danh sách các cuốn sách.
     */
    public List<Book> readBooks() {
        File xmlFile = new File(xmlFilePath);
        if (!xmlFile.exists()) {
            System.out.println("File XML '" + xmlFilePath + "' not found. Returning empty list.");
            return new ArrayList<>(); // Trả về danh sách rỗng nếu file không tồn tại
        }

        try {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            // Unmarshal (đọc) file XML và chuyển đổi thành đối tượng Library
            Library library = (Library) unmarshaller.unmarshal(new FileReader(xmlFilePath));
            return library.getBooks() != null ? library.getBooks() : new ArrayList<>();
        } catch (JAXBException e) {
            System.err.println("Error reading XML file (JAXB): " + e.getMessage());
            e.printStackTrace(); // In chi tiết lỗi
            // Có thể xảy ra nếu XML không đúng định dạng hoặc lỗi phân tích cú pháp
            return new ArrayList<>(); // Trả về danh sách rỗng khi có lỗi
        } catch (FileNotFoundException e) {
            // Trường hợp này đã được xử lý ở trên, nhưng vẫn bắt để đề phòng
            System.err.println("Error reading XML file (File not found): " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Ghi danh sách sách vào file XML.
     *
     * @param books Danh sách sách cần ghi.
     */
    public void writeBooks(List<Book> books) {
        Library library = new Library();
        library.setBooks(books);

        try {
            Marshaller marshaller = context.createMarshaller();
            // Định dạng output XML cho dễ đọc
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            // Marshal (ghi) đối tượng Library vào file XML
            marshaller.marshal(library, new File(xmlFilePath));
            System.out.println("Books successfully saved to " + xmlFilePath);
        } catch (JAXBException e) {
            System.err.println("Error writing XML file: " + e.getMessage());
            e.printStackTrace(); // In chi tiết lỗi
        }
    }
}