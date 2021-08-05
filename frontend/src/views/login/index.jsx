import { Button, Card, CardContent, CardHeader, Container, Grid, makeStyles, TextField } from "@material-ui/core";
import { useSnackbar } from "notistack";
import { useState } from "react";
import { useHistory } from "react-router-dom";
import { login, setToken } from "../../services/auth.service";

const useStyles = makeStyles((theme) => ({
    root: {
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        height: "100%"
    }
}));

export default function LoginView(props) {
    const classes = useStyles();
    const history = useHistory();
    const { enqueueSnackbar } = useSnackbar();

    const [username, setUsername] = useState();
    const [password, setPassword] = useState();

    const handleLogin = (e) => {
        e.preventDefault();
        login(username, password)
            .then(response => {
                setToken(response.jwt);
                enqueueSnackbar("Login successful!", { variant: "success" });
                history.push("/home");
            })
            .catch(e => {
                enqueueSnackbar("Login failed!", { variant: "error" });
            });
    };

    return (
        <Container maxWidth={false} className={classes.root}>
            <Container maxWidth={"sm"}>
                <Card>
                    <CardHeader title={"Login"} />
                    <CardContent>
                        <form onSubmit={handleLogin}>
                            <Grid container
                                display={"flex"}
                                direction={"column"}
                                spacing={2}
                            >
                                <Grid item>
                                    <TextField
                                        variant="outlined"
                                        placeholder={"Username"}
                                        value={username ?? ""}
                                        onChange={e => setUsername(e.target.value)}
                                        fullWidth
                                    />
                                </Grid>
                                <Grid item>
                                    <TextField
                                        variant="outlined"
                                        placeholder={"Password"}
                                        type={"password"}
                                        value={password ?? ""}
                                        onChange={e => setPassword(e.target.value)}
                                        fullWidth
                                    />
                                </Grid>
                                <Grid item>
                                    <Button
                                        variant={"contained"}
                                        color={"primary"}
                                        type="submit"
                                    >
                                        Login
                                    </Button>
                                </Grid>
                            </Grid>
                        </form>
                    </CardContent>
                </Card>
            </Container>
        </Container>
    );
}