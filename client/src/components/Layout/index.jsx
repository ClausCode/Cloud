import styles from "./index.module.scss";
import {Navigation} from "../index.js";
import {Navigate, Outlet} from "react-router-dom";
import {useSelector} from "react-redux";

const Layout = () => {
    if(!useSelector(state => state.auth.user)) return <Navigate to={'/sign-in'}/>
    

    return <main className={styles.wrapper}>
        <div className={styles.sidebar}>
            <Navigation/>
        </div>
        <div className={styles.content}>
            <Outlet/>
        </div>
    </main>;
}

export default Layout;