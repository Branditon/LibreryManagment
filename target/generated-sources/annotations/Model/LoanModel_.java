package Model;

import Model.BookModel;
import Model.UserModel;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.12.v20230209-rNA", date="2025-04-23T17:18:34")
@StaticMetamodel(LoanModel.class)
public class LoanModel_ { 

    public static volatile SingularAttribute<LoanModel, Date> returnDate;
    public static volatile SingularAttribute<LoanModel, BookModel> book;
    public static volatile SingularAttribute<LoanModel, Date> loanDate;
    public static volatile SingularAttribute<LoanModel, Long> id;
    public static volatile SingularAttribute<LoanModel, UserModel> user;
    public static volatile SingularAttribute<LoanModel, String> status;

}