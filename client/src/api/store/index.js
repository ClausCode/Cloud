import {combineReducers, configureStore} from "@reduxjs/toolkit";
import userSlice from "./slice/userSlice.js";
import infoSlice from "./slice/infoSlice.js";

const root = combineReducers({
    auth: userSlice,
    info: infoSlice,
})

const store = configureStore({
    reducer: root
})

export default store