import styles from './index.module.scss'
import {Form} from "/src/components";
import {Link, Navigate} from "react-router-dom";
import {useForm} from "react-hook-form";
import {signUp} from "../../api/service/AuthService.js";
import {setUser} from "../../api/store/slice/userSlice.js";
import {useDispatch, useSelector} from "react-redux";
import {Error} from "../../components/index.js";
import {useState} from "react";
import {showMessage} from "../../api/service/InfoService.js";

const equalsPassword = (password) => (value) => {
    return password === value
}

const SignUpPage = () => {
    const dispatch = useDispatch();
    const [password, setPassword] = useState('');
    const {register, formState: {errors}, handleSubmit} = useForm({mode: "all"});

    if(useSelector(state => state.auth.user)) return <Navigate to={'/space'}/>

    async function onSubmit(fields) {
        await signUp(fields.email, fields.username, fields.password)
            .then(response => {
                showMessage('Аккаунт создан')
                response.json().then(result => {
                    localStorage.setItem('accessToken', result.token)
                    dispatch(setUser(result.user))
                })
            }).catch(error => {
                if (error && !error.response) {
                    showMessage('Проблемы с подключением к серверу', 'error')
                    throw "Server Error!"
                }
                switch (error.response.status) {
                    case 400:
                        showMessage('Почтовый адрес уже занят, используйте другой', 'error')
                        throw "Email already exists!"
                    default:
                        showMessage('Что-то пошло не так', 'warning')
                        throw "Error!"
                }
            });
    }

    return <main className={styles.container}>
        <Form handle={handleSubmit(onSubmit)} title={"Регистрация"}>
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

            <input type="text" placeholder="Имя пользователя" maxLength={64}
                   {...register('username', {
                       required: "Поле обязательно к заполнению!",
                       minLength: {
                           message: "Не менее 3-х символов!",
                           value: 3
                       }
                   })}
            />

            {
                errors.username &&
                <Error>
                    {errors.username.message}
                </Error>
            }

            <input type="password" placeholder="Пароль" maxLength={64} value={password}
                   onInput={e => setPassword(e.target.value)}
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

            <input type="password" placeholder="Повтор пароля" maxLength={64}
                   {...register('equalsPassword', {
                       required: "Поле обязательно к заполнению!",
                       validate: equalsPassword(password)
                   })}
            />

            {
                errors.equalsPassword &&
                <Error>
                    Пароли не совпадают!
                </Error>
            }

            <input type="submit" value={"Создать аккаунт"}/>
            <Link to={"/sign-in"}>Войти в аккаунт</Link>
        </Form>
    </main>
}

export default SignUpPage;