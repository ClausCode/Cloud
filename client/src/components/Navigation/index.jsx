import styles from './index.module.scss';

import Logo from '/src/assets/logo.svg';
import Disk from '/src/assets/icons/disk.svg';
import Group from '/src/assets/icons/group.svg';
import Clock from '/src/assets/icons/clock.svg';
import Star from '/src/assets/icons/star.svg';
import Bin from '/src/assets/icons/bin.svg';
import Cloud from '/src/assets/icons/cloud.svg';
import Logout from '/src/assets/icons/logout.svg';

import Account from '/src/assets/test/avatar.jpg';

import {Link} from 'react-router-dom';
import {NavItem} from "../index.js";

const Navigation = () => {
    return <nav className={styles.navigation}>
        <Link to={"/space"} className={styles.header}>
            <img src={Logo} alt="Logo"/>
            <span>You Cloud</span>
        </Link>

        <NavItem
            link="/space/account"
            icon={Account}
            title="Настройки аккаунта"
        />

        <NavItem
            link="/space"
            icon={Disk}
            title="Мой диск"
        />

        <NavItem
            link="/space/available"
            icon={Group}
            title="Доступные мне"
            badge={99}
        />

        <NavItem
            link="/space/recent"
            icon={Clock}
            title="Недавние"
        />

        <NavItem
            link="/space/favorites"
            icon={Star}
            title="Помеченные"
        />

        <NavItem
            link="/space/bin"
            icon={Bin}
            title="Корзина"
        />

        <NavItem
            link="/space/storage"
            icon={Cloud}
            title="Хранилище"
        />

        <NavItem
            style={{color: "#FF6060"}}
            link="/logout"
            icon={Logout}
            title="Выход"
        />
    </nav>
}

export default Navigation;