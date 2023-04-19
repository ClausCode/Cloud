import {createSlice} from "@reduxjs/toolkit";

const userSlice = createSlice({
    name: "AUTH",
    initialState: {
        user: null
    },
    reducers: {
        setUser(state, action) {
            state.user = action.payload;
        },
        setUserMFA(state, action) {
            state.user.mfa = action.payload;
        }
    }
})

export default userSlice.reducer
export const { setUser, setUserMFA } = userSlice.actions


