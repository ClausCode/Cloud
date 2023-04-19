import {useSelector} from "react-redux";
import {changeName, changePassword} from "/src/api/service/UserService.js";
import {showMessage} from "/src/api/service/InfoService.js";
import {Link} from "react-router-dom"

const AccountMainPage = () => {
    const user = useSelector(state => state.auth.user);

    const changeNameAction = () => {
        const name = prompt('Введи новое имя', user.name);
        if (name) {
            changeName(name).then(() => {
                showMessage('Вы успешно сменили имя на ' + name)
            })
        }
    }

    const changePasswordAction = () => {
        const oldPassword = prompt('Введи текущий пароль', '');
        if (!oldPassword) return;
        const newPassword = prompt('Введи новый пароль', '');
        if (!newPassword) return;

        changePassword(oldPassword, newPassword).then()
    }

    const updated = new Date(user.updated * 1000);

    return <>
        <p>Пользователь: {user.name}
            <button onClick={changeNameAction}>Изменить</button>
        </p>
        <p>Почта: {user.email}</p>
        <p>Многофакторная аутентификация: {user.tfa.toString()}
            <Link to={'/space/account/mfa'}><button>Изменить</button></Link>
        </p>
        <p>Пароль <button onClick={changePasswordAction}>Изменить</button></p>
        <br/>
        <p>Дата регистрации: {new Date(user.created * 1000).toLocaleDateString()}</p>
        <p>Последняя смена пароля: {updated.toLocaleDateString() + ' ' + updated.toLocaleTimeString()}</p>
    </>
}

export default AccountMainPage