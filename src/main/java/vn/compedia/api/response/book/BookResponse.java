package vn.compedia.api.response.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BookResponse {

    @JsonProperty("book_id")
    private Long bookId;

    @JsonProperty("book_name")
    private String bookName;

    @JsonProperty("id_author")
    private Long idAuthor;

    @JsonProperty("name_author")
    private String nameAuthor;


    @JsonProperty("id_type_book")
    private Long idTypeBook;

    @JsonProperty("category_name")
    private String categoryName;

    @JsonProperty("page_number")
    private Integer pageNumber;

    @JsonProperty("publishing_year")
    private String publishingYear;

    @JsonProperty("price")
    private Integer price;

    @JsonProperty("amount")
    private Integer amount;

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("company_id")
    private Long companyId;

    @JsonProperty("publish_name")
    private String publishName;

    @JsonProperty("image")
    private String image;

    @JsonProperty("note")
    private String note;

}
