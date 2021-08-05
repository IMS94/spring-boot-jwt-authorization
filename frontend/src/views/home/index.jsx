import { Button, AppBar, Card, CardContent, CircularProgress, Container, Grid, IconButton, makeStyles, Toolbar, Typography, Box } from "@material-ui/core";
import { Alert, AlertTitle } from "@material-ui/lab";
import { useSnackbar } from "notistack";
import { useEffect, useState } from "react";
import { getUserInfo } from "../../services/user.service";
import { Person, Menu as MenuIcon } from "@material-ui/icons";
import authService from "../../services/auth.service";
import { useHistory } from "react-router-dom";

const useStyles = makeStyles((theme) => ({
    root: {
        height: "100%"
    },
    grid: {
        height: "100%"
    },
    logoutBtn: {
        marginLeft: "auto"
    }
}));


function HomeView() {
    const { enqueueSnackbar } = useSnackbar();
    const history = useHistory();
    const classes = useStyles();

    const [user, setUser] = useState();
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        setLoading(true);
        getUserInfo()
            .then(user => setUser(user))
            .catch(e => enqueueSnackbar(e, { variant: "error" }))
            .finally(() => setLoading(false));
    }, []);

    const logout = () => {
        authService.logout();
        history.push("/");
    }

    return (
        <Container maxWidth={false} className={classes.root} disableGutters>
            <AppBar position="static">
                <Toolbar>
                    <IconButton edge="start" className={classes.menuButton} color="inherit" aria-label="menu">
                        <MenuIcon />
                    </IconButton>
                    <Typography variant="h6" className={classes.title}>
                        Demo
                    </Typography>
                    <Button color="inherit" onClick={logout} className={classes.logoutBtn}>Logout</Button>
                </Toolbar>
            </AppBar>
            <Grid container
                justifyContent={"center"}
                alignContent={"center"}
                className={classes.grid}
                spacing={3}
            >
                <Grid item xs={6}>
                    <Card>
                        <CardContent>
                            {loading && (
                                <CircularProgress />
                            )}

                            {!!user && (
                                <Box
                                    display="flex"
                                    alignItems={"center"}
                                    flexDirection="column"
                                >
                                    <Person />
                                    <Box width={0.5}>
                                        <Alert>
                                            <AlertTitle>Hello {user.username}!</AlertTitle>
                                            Welcome back!
                                        </Alert>
                                    </Box>
                                </Box>
                            )}
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </Container>
    );
}

export default HomeView;