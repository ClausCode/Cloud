import styles from './index.module.scss'

import {Form} from "/src/components";
import {Link, Navigate} from "react-router-dom";
import {useForm} from "react-hook-form";
import {signIn} from "../../api/service/AuthService.js";
import {setUser} from "../../api/store/slice/userSlice.js";
import {useDispatch, useSelector} from "react-redux";
import {Error} from "../../components/index.js";
import {showMessage} from "../../api/service/InfoService.js";

const SignInPage = () => {
    const dispatch = useDispatch();
    const {register, formState: {errors}, handleSubmit} = useForm({mode: "onChange"});

    if (useSelector(state => state.auth.user)) return <Navigate to={'/space'}/>

    async function onSubmit(fields) {
        await signIn(fields.email, fields.password, fields.token)
            .then(response => {
                response.json().then(result => {
                    showMessage('Вы авторизованы')
                    localStorage.setItem('accessToken', result.token)
                    dispatch(setUser(result.user))
                })
            }).catch(error => {
                if (!error.response) {
                    showMessage('Проблемы с подключением к серверу', 'error')
                    throw "Server Error!"
                }
                switch (error.response.status) {
                    case 400:
                        showMessage('Не верный логин или пароль', 'error')
                        throw "Uncorrected Email or Password!"
                    case 423:
                        showMessage('Код указан не верно!', 'error')
                        throw "Uncorrected Code!"
                    default:
                        showMessage('Что-то пошло не так', 'warning')
                        throw "Error!"
                }
            });
    }

    return <main className={styles.container}>
        <Form title={"Авторизация"} handle={handleSubmit(onSubmit)}>

            <input type="email" placeholder="Почта" maxLength={64}
                   {...register('email', {
                       required: "Поле обязательно к заполнению!",
                       minLength: {
                           message: "Не менее 8-ми символов!",
                           value: 8
                       }
                   })}
            />

            {
                errors.email &&
                <Error>
                    {errors.email.message}
                </Error>
            }

            <input type="password" placeholder="Пароль" maxLength={64}
                   {...register('password', {
                       required: "Поле обязательно к заполнению!",
                       minLength: {
                           message: "Не менее 8-ми символов!",
                           value: 8
                       }
                   })}
            />

            {
                errors.password &&
                <Error>
                    {errors.password.message}
                </Error>
            }

            <input type="number" placeholder="Код" maxLength="6"
                   {...register('token', {
                       maxLength: {
                           message: "Не больше 6-и символов!",
                           value: 6
                       }
                   })}
            />

            {
                errors.token &&
                <Error>
                    {errors.token.message}
                </Error>
            }

            <input type="submit" value={"Вход"}/>
            <Link to={"/sign-up"}>Создать аккаунт</Link>
        </Form>
    </main>
}

export default SignInPage;