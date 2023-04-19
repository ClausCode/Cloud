import ky from "ky";
import {refresh} from "./service/AuthService";

export const API_URL = `http://${prompt('Введи адрес сервера', 'localhost')}:8080/api/v1/`

export const $API = ky.create({
    credentials: "include",
    prefixUrl: API_URL,
});

export const $secureAPI = () => $API.extend({
    headers: {
        Authorization: `Bearer_${localStorage.getItem("accessToken")}`
    },
    retry: {
        limit: 2,
        methods: ['get', 'post', 'update', 'delete', 'put'],
        statusCodes: [403],
    },
    hooks: {
        beforeRetry: [
            async ({request}) => {
                await refresh().then(result => {
                    localStorage.setItem("accessToken", result.accessToken);
                    request.headers.set("Authorization", `Bearer_${result.accessToken}`);
                })
            }
        ]
    }
})


