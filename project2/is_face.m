function [mse coeffs] = is_face(avgface, eigfaces, face)
[r c d] = size(face);
coeffs = project_face(avgface, eigfaces, face);
construct = construct_face(avgface, eigfaces, coeffs);
mse = my_mse(face, construct);
end