import {useSelector} from "react-redux";

const SpacePage = () => {
    document.title = "You Cloud | Моё пространство"

    const user = useSelector(state => state.auth.user)

    return <>Content: User[{user.name}]</>
}

export default SpacePage;