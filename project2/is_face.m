function mse = is_face(avgface, eigfaces, face)
coeffs = project_face(avgface, eigfaces, face);
construct = construct_face(avgface, eigfaces, coeffs);
mse = mean(mean((face - construct).^2));
end

