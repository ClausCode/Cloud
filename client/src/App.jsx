import {SignInPage, SignUpPage, SpacePage} from "./pages/index.js";
import {BrowserRouter, Navigate, Route, Routes} from "react-router-dom";
import {InfoCenter, Layout} from "./components/index.js";
import React, {useEffect, useState} from "react";
import {refresh} from "./api/service/AuthService.js";
import LogoutPage from "./pages/LogoutPage/index.jsx";
import {AccountMainPage, AccountMFAPage} from "./pages/account/index.js";
import {useDispatch} from "react-redux";
import {setUser} from "./api/store/slice/userSlice.js";
import {showMessage} from "./api/service/InfoService.js";

function App() {
    const dispatch = useDispatch()
    const [isLoad, setLoad] = useState(false)

    useEffect(() => {
        if (!isLoad)
            refresh().then(response => {
                response.json().then(result => {
                    localStorage.setItem('accessToken', result.token)
                    dispatch(setUser(result.user))

                    setLoad(true)
                })
            }).catch(error => {
                setLoad(true);
                if (error.response.status === 423) {
                    showMessage('Что-то пошло не так, требуется авторизация!', 'error')
                }
            });
    })

    if (!isLoad) return <>Проверка...</>

    return (
        <>
            <InfoCenter/>
            <BrowserRouter>
                <Routes>
                    <Route path="/">
                        <Route index element={<Navigate to={"/sign-in"}/>}/>
                        <Route path="sign-in" element={<SignInPage/>}/>
                        <Route path="sign-up" element={<SignUpPage/>}/>
                        <Route path="space" element={<Layout/>}>
                            <Route index element={<SpacePage/>}/>
                            <Route path="account">
                                <Route index element={<AccountMainPage/>}/>
                                <Route path="mfa" element={<AccountMFAPage/>}/>
                            </Route>
                            <Route path="available" element={<>Available</>}/>
                            <Route path="recent" element={<>Recent</>}/>
                            <Route path="favorites" element={<>Favorites</>}/>
                            <Route path="bin" element={<>Bin</>}/>
                            <Route path="storage" element={<>Storage</>}/>
                        </Route>
                        <Route path="logout" element={<LogoutPage/>}/>
                        <Route path="*" element={<span>404</span>}/>
                    </Route>
                </Routes>
            </BrowserRouter>
        </>
    );
}

export default App;