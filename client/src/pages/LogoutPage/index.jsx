import {Navigate} from "react-router-dom";
import {logout} from "../../api/service/AuthService.js";

const LogoutPage = () => {
    logout().then()
    return <Navigate to={"/"}/>
}

export default LogoutPage