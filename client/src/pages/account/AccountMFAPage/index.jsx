import {getQrCode, switchMFA} from "../../../api/service/UserService.js";
import {useDispatch, useSelector} from "react-redux";
import {setUser, setUserMFA} from "../../../api/store/slice/userSlice.js";
import {showMessage} from "../../../api/service/InfoService.js";
import {useForm} from "react-hook-form";

const AccountMFAPage = () => {
    const {register, formState: {errors}, handleSubmit} = useForm({mode: "onSubmit"});
    const user = useSelector(state => state.auth.user)
    const dispatch = useDispatch()

    if (!user.mfa) {
        getQrCode().then(result => {
            dispatch(setUserMFA(result))
        })
    }

    async function onSubmit(fields) {
        switchMFA(fields.code)
            .then(result => {
                showMessage('Состояние MFA: ' + result.tfa)
                dispatch(setUser(result))
            })
    }

    return <>
        <img src={user.mfa && user.mfa.qrCode} alt='QrCode'/>
        <h4>{user.mfa && user.mfa.secret}</h4>

        {
            user.tfa ? <>
                    <button onClick={() => onSubmit({})}>Отключить</button>
                </>
                : <>
                <form onSubmit={handleSubmit(onSubmit)}>
                        <input type="text" placeholder="Введите код" maxLength={6} minLength={1}
                            {...register('code', {
                                required: "Поле обязательно к заполнению!",
                            })}
                        />
                        <input type="submit" value="Включить"/>
                    </form>
                </>

        }
    </>
}

export default AccountMFAPage;