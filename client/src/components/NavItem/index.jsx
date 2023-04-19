import styles from './index.module.scss'

import {Link} from "react-router-dom";

const NavItem = ({style = null, link, icon = null, title, badge = null}) => {
    return <Link to={link} className={styles.nav_item}>
        <div className={styles.content}>
            {icon && <img className={styles.icon} src={icon} alt="Icon"/>}
            <span style={style} className={styles.title}>{title}</span>
        </div>
        {badge && <span className={styles.bage}>{badge}</span>}
    </Link>
}

export default NavItem;