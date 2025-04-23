package Model;

import Model.LoanModel;
import Model.RolModel;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.12.v20230209-rNA", date="2025-04-23T17:18:34")
@StaticMetamodel(UserModel.class)
public class UserModel_ { 

    public static volatile SingularAttribute<UserModel, String> lastName;
    public static volatile SingularAttribute<UserModel, String> password;
    public static volatile ListAttribute<UserModel, LoanModel> loan;
    public static volatile SingularAttribute<UserModel, String> name;
    public static volatile SingularAttribute<UserModel, Long> id;
    public static volatile SingularAttribute<UserModel, String> email;
    public static volatile SingularAttribute<UserModel, RolModel> rol;

}