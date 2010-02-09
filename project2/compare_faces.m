function mse = compare_faces(avgface, eigfaces, face1, face2)
[r c] = size(face1);
[r2 c2] = size(face2);
face1 = my_imresize(face1, min(r, r2), min(c, c2));
face2 = my_imresize(face2, min(r, r2), min(c, c2));
face1 = face1 / norm(face1);
face2 = face2 / norm(face2);
mse = my_mse(project_face(avgface, eigfaces, face1),...
             project_face(avgface, eigfaces, face2));
end

