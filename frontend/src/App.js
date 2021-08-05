import './App.css';
import { Container, createTheme, ThemeProvider, makeStyles } from "@material-ui/core";
import { BrowserRouter, Redirect, Route, Switch } from "react-router-dom";
import { SnackbarProvider } from 'notistack';
import LoginView from './views/login';
import HomeView from './views/home';
import ProtectedRoute from './components/ProtectedRoute';

const theme = createTheme({});

const useStyles = makeStyles((theme) => ({
  root: {
    height: "100vh",
    backgroundColor: theme.palette.grey[500]
  }
}));

function App() {
  const classes = useStyles();

  return (
    <ThemeProvider theme={theme}>
      <SnackbarProvider maxSnack={3}>
        <BrowserRouter>
          <Container maxWidth={false} className={classes.root} disableGutters>
            <Switch>
              <Route path="/login" component={LoginView} />
              <ProtectedRoute path="/home" component={HomeView} />
              <Redirect to={"/login"} />
            </Switch>
          </Container>
        </BrowserRouter>
      </SnackbarProvider>
    </ThemeProvider>
  );
}

export default App;
